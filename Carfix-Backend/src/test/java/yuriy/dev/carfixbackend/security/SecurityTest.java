package yuriy.dev.carfixbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import yuriy.dev.carfixbackend.controller.rest.OrderController;
import yuriy.dev.carfixbackend.controller.rest.WorkController;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.service.CustomUserDetailService;
import yuriy.dev.carfixbackend.service.OrderService;
import yuriy.dev.carfixbackend.service.WorkService;
import yuriy.dev.carfixbackend.token.JwtUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты ролевой модели доступа к REST API.
 * Сервисный слой и БД замоканы — проверяются только правила SecurityConfig.
 * Автоматизирует тест-кейс №3 из практической работы №4 (запросы без полномочий администратора).
 */
@WebMvcTest({WorkController.class, OrderController.class})
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkService workService;
    @MockitoBean
    private OrderService orderService;

    // Зависимости конфигурации безопасности (SecurityConfig / JwtRequestFilter)
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void getOrders_unauthorized_forbidden() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getWorks_unauthorized_isPublic() throws Exception {
        when(workService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/works"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @WithMockUser(username = "user@mail.ru", roles = "USER")
    void createWork_asRegularUser_forbidden() throws Exception {
        WorkDto workDto = new WorkDto(null, "test", "test", new BigDecimal("1000"), null);

        mockMvc.perform(post("/api/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.ru", roles = "USER")
    void updateWork_asRegularUser_forbidden() throws Exception {
        WorkDto workDto = new WorkDto(UUID.randomUUID(), "test", "test", new BigDecimal("1200"), null);

        mockMvc.perform(put("/api/works/{id}", workDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workDto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@mail.ru", roles = "USER")
    void deleteWork_asRegularUser_forbidden() throws Exception {
        mockMvc.perform(delete("/api/works/{id}", UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@mail.ru", roles = "ADMIN")
    void createWork_asAdmin_allowed() throws Exception {
        WorkDto workDto = new WorkDto(null, "testWork", "testDescription", new BigDecimal("1000"), null);
        when(workService.createWork(any(WorkDto.class)))
                .thenReturn(new WorkDto(UUID.randomUUID(), "testWork", "testDescription", new BigDecimal("1000"), null));

        mockMvc.perform(post("/api/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("testWork"));
    }

    @Test
    @WithMockUser(username = "admin@mail.ru", roles = "ADMIN")
    void deleteWork_asAdmin_allowed() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/works/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(workService).deleteWork(id);
    }

    /**
     * Вредоносная строка в поле названия должна обрабатываться как обычные данные.
     * Защита от SQL-инъекций обеспечивается параметризацией запросов JPA/Hibernate —
     * контроллер обязан передать строку в сервис без изменений.
     */
    @Test
    @WithMockUser(username = "admin@mail.ru", roles = "ADMIN")
    void sqlInjectionAttempt_isTreatedAsPlainData() throws Exception {
        String maliciousInput = "test'); DROP TABLE \"user\"; --";
        WorkDto workDto = new WorkDto(null, maliciousInput, null, BigDecimal.TEN, null);
        when(workService.createWork(any(WorkDto.class)))
                .thenReturn(new WorkDto(UUID.randomUUID(), maliciousInput, null, BigDecimal.TEN, null));

        mockMvc.perform(post("/api/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workDto)))
                .andDo(print())
                .andExpect(status().isOk());

        ArgumentCaptor<WorkDto> captor = ArgumentCaptor.forClass(WorkDto.class);
        verify(workService).createWork(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo(maliciousInput);
    }
}
