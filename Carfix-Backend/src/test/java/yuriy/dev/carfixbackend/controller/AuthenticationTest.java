package yuriy.dev.carfixbackend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import yuriy.dev.carfixbackend.auth.AuthenticationService;
import yuriy.dev.carfixbackend.controller.rest.AuthenticationController;
import yuriy.dev.carfixbackend.dto.response.JwtAuthenticationResponse;
import yuriy.dev.carfixbackend.service.CustomUserDetailService;
import yuriy.dev.carfixbackend.token.JwtUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты REST-контроллера аутентификации.
 * Слой сервисов и БД замоканы — тесты изолированы и не зависят от окружения.
 */
@WebMvcTest(AuthenticationController.class)
class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    // Зависимости конфигурации безопасности (SecurityConfig / JwtRequestFilter)
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void register_validData_returnsJwtToken() throws Exception {
        when(authenticationService.signUpJwt(any()))
                .thenReturn(new JwtAuthenticationResponse("test-jwt-token"));

        String requestBody = """
                {
                    "username": "test@user.ru",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe",
                    "phoneNumber": "+79991234567"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    void register_invalidData_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "password": "pass",
                                    "firstName": "",
                                    "lastName": "",
                                    "phoneNumber": "123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidEmail_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "not-an-email",
                                    "password": "password123",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "phoneNumber": "+79991234567"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_validCredentials_returnsJwtToken() throws Exception {
        when(authenticationService.signInJwt(any()))
                .thenReturn(new JwtAuthenticationResponse("login-jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "test@user.ru",
                                    "password": "password123"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-jwt-token"));
    }

    @Test
    void login_blankPassword_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "test@user.ru",
                                    "password": ""
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
