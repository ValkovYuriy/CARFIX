package yuriy.dev.carfixbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import yuriy.dev.carfixbackend.auth.Role;
import yuriy.dev.carfixbackend.controller.rest.OrderController;
import yuriy.dev.carfixbackend.dto.CarDto;
import yuriy.dev.carfixbackend.dto.ModelDto;
import yuriy.dev.carfixbackend.dto.OrderDto;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.dto.enums.Status;
import yuriy.dev.carfixbackend.model.Mark;
import yuriy.dev.carfixbackend.service.CustomUserDetailService;
import yuriy.dev.carfixbackend.service.OrderService;
import yuriy.dev.carfixbackend.token.JwtUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты REST-контроллера заказов.
 * Сервисный слой и БД замоканы — тесты изолированы и не зависят от окружения.
 * Бизнес-логика создания заказа покрыта юнит-тестами {@link yuriy.dev.carfixbackend.service.OrderServiceTest}.
 */
@WebMvcTest(OrderController.class)
class OrdersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    // Зависимости конфигурации безопасности (SecurityConfig / JwtRequestFilter)
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    private OrderDto buildRequestDto() {
        Mark mark = Mark.builder().id(UUID.randomUUID()).markName("Acura").build();
        ModelDto modelDto = new ModelDto(UUID.randomUUID(), "CDX", mark);
        CarDto carDto = new CarDto(null, "А123ВС163", "1HGCM82633A123456", modelDto);
        UserDto userDto = new UserDto(UUID.randomUUID(), "test@mail.ru", null, "+79990001122", "Иван", "Иванов", Role.ROLE_USER);
        List<WorkDto> works = List.of(
                new WorkDto(UUID.randomUUID(), "Замена масла", null, new BigDecimal("1200"), null),
                new WorkDto(UUID.randomUUID(), "Диагностика", null, new BigDecimal("800"), null)
        );
        return new OrderDto(null, carDto, userDto, LocalDateTime.now(),
                new BigDecimal("2000"), Status.PENDING, "Тестовый заказ", works);
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = "USER")
    void addOrder_authorizedUser_returnsCreatedOrder() throws Exception {
        OrderDto requestDto = buildRequestDto();
        OrderDto createdDto = new OrderDto(UUID.randomUUID(), requestDto.carDto(), requestDto.userDto(),
                requestDto.orderDate(), new BigDecimal("2000"), Status.PENDING, "Тестовый заказ", requestDto.works());

        when(orderService.addOrder(any(OrderDto.class), anyInt())).thenReturn(createdDto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .queryParam("timezoneOffset", "-240"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.id").value(createdDto.id().toString()))
                .andExpect(jsonPath("$.data.price").value(2000))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(orderService).addOrder(any(OrderDto.class), eq(-240));
    }

    @Test
    void addOrder_unauthorizedUser_forbidden() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequestDto()))
                        .queryParam("timezoneOffset", "-240"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = "USER")
    void findAllOrders_authorizedUser_returnsList() throws Exception {
        OrderDto dto = new OrderDto(UUID.randomUUID(), null, null, LocalDateTime.now(),
                new BigDecimal("2000"), Status.ACCEPTED, "Заказ", List.of());

        when(orderService.findAllOrders(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data[0].id").value(dto.id().toString()));
    }

    @Test
    @WithMockUser(username = "test@mail.ru", roles = "USER")
    void findOrderById_returnsOrder() throws Exception {
        UUID id = UUID.randomUUID();
        OrderDto dto = new OrderDto(id, null, null, LocalDateTime.now(),
                new BigDecimal("1500"), Status.PENDING, "Заказ", List.of());

        when(orderService.findOrderById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/orders/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()));
    }

    @Test
    @WithMockUser(username = "admin@mail.ru", roles = "ADMIN")
    void deleteOrder_asAdmin_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/orders/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());

        verify(orderService).deleteOrder(id);
    }
}
