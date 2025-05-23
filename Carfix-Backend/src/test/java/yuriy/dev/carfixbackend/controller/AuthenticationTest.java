package yuriy.dev.carfixbackend.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import yuriy.dev.carfixbackend.token.JwtUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    public void testRegisterUser_ReturnsValidJwtToken() throws Exception {
        String requestBody = """
                {
                    "username": "test@user.ru",
                    "password": "password123",
                    "firstName": "John",
                    "lastName": "Doe",
                    "phoneNumber": "+79991234567"
                }
                """;

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = JsonPath.parse(responseJson).read("$.token", String.class);
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@user.ru");
        assertThat(jwtUtil.isTokenValid(token, userDetails)).isTrue();
        assertThat(jwtUtil.extractUserName(token)).isEqualTo("test@user.ru");
    }

    @Test
    public void testRegisterUser_InvalidData() throws Exception {
        mockMvc.perform(post("/auth/register")
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
}
