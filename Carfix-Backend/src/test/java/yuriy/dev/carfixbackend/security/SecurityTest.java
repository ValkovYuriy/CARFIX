package yuriy.dev.carfixbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import yuriy.dev.carfixbackend.dto.WorkDto;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testAuthorizedAccess() throws Exception {
        String requestBody = """
                {
                    "name": "testWork",
                    "description": "testDescription",
                    "workPrice": 1000,
                    "imageUrl": "url"
                }
        """;
        mockMvc.perform(post("/api/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(user("admin@mail.ru").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testSqlInjectionAttempt() throws Exception {
        String maliciousInput = "test'); DROP TABLE \"user\"; --";
        WorkDto workDto = new WorkDto(null,maliciousInput,null, BigDecimal.TEN,null);
        String requestContent = objectMapper.writeValueAsString(workDto);
        mockMvc.perform(post("/api/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent)
                        .with(user("admin@mail.ru").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(4);
    }
}
