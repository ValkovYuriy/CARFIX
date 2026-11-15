package yuriy.dev.carfixbackend.token;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import yuriy.dev.carfixbackend.auth.Role;
import yuriy.dev.carfixbackend.model.User;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private User user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Тестовый ключ (base64), не связанный с боевой конфигурацией
        ReflectionTestUtils.setField(jwtUtil, "jwtSigningKey",
                "Y2FyZml4LWp3dC11bml0LXRlc3Qtc2lnbmluZy1rZXktMjU2Yml0cyE=");

        user = User.builder()
                .id(UUID.randomUUID())
                .username("user@mail.ru")
                .password("encoded")
                .firstName("Иван")
                .lastName("Иванов")
                .phoneNumber("+79990001122")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void generateToken_containsUsernameAsSubject() {
        String token = jwtUtil.generateToken(user);

        assertThat(jwtUtil.extractUserName(token)).isEqualTo("user@mail.ru");
    }

    @Test
    void isTokenValid_returnsTrue_forFreshTokenAndSameUser() {
        String token = jwtUtil.generateToken(user);

        assertThat(jwtUtil.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalse_forAnotherUser() {
        String token = jwtUtil.generateToken(user);

        User another = User.builder()
                .id(UUID.randomUUID())
                .username("other@mail.ru")
                .role(Role.ROLE_USER)
                .build();

        assertThat(jwtUtil.isTokenValid(token, another)).isFalse();
    }

    @Test
    void extractUserName_throws_forTamperedToken() {
        String token = jwtUtil.generateToken(user);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtUtil.extractUserName(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void extractUserName_throws_forGarbageToken() {
        assertThatThrownBy(() -> jwtUtil.extractUserName("not.a.jwt"))
                .isInstanceOf(JwtException.class);
    }
}
