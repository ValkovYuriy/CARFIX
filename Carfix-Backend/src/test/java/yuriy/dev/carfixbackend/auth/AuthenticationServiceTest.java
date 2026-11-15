package yuriy.dev.carfixbackend.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.dto.request.SignInRequest;
import yuriy.dev.carfixbackend.dto.request.SignUpRequest;
import yuriy.dev.carfixbackend.dto.response.JwtAuthenticationResponse;
import yuriy.dev.carfixbackend.mapper.UserMapper;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.service.UserService;
import yuriy.dev.carfixbackend.token.JwtUtil;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void signUpJwt_encodesPassword_assignsUserRole_andReturnsToken() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("new@mail.ru");
        request.setPassword("raw-password");
        request.setFirstName("Иван");
        request.setLastName("Иванов");
        request.setPhoneNumber("+79990001122");

        UserDto mappedDto = new UserDto(null, "new@mail.ru", "encoded", "+79990001122", "Иван", "Иванов", Role.ROLE_USER);
        UserDto savedDto = new UserDto(UUID.randomUUID(), "new@mail.ru", "encoded", "+79990001122", "Иван", "Иванов", Role.ROLE_USER);
        User savedUser = User.builder()
                .id(savedDto.id())
                .username("new@mail.ru")
                .role(Role.ROLE_USER)
                .build();

        when(passwordEncoder.encode("raw-password")).thenReturn("encoded");
        when(userMapper.toDto(any(User.class))).thenReturn(mappedDto);
        when(userService.addUser(mappedDto)).thenReturn(savedDto);
        when(userMapper.toUser(savedDto)).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser)).thenReturn("jwt-token");

        JwtAuthenticationResponse response = authenticationService.signUpJwt(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).toDto(userCaptor.capture());
        User builtUser = userCaptor.getValue();
        assertThat(builtUser.getPassword()).isEqualTo("encoded");
        assertThat(builtUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(builtUser.getUsername()).isEqualTo("new@mail.ru");
    }

    @Test
    void signInJwt_validCredentials_returnsToken() {
        SignInRequest request = new SignInRequest();
        request.setUsername("user@mail.ru");
        request.setPassword("password123");

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("user@mail.ru")
                .role(Role.ROLE_USER)
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        JwtAuthenticationResponse response = authenticationService.signInJwt(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void signInJwt_invalidCredentials_propagatesException() {
        SignInRequest request = new SignInRequest();
        request.setUsername("user@mail.ru");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authenticationService.signInJwt(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
