package yuriy.dev.carfixbackend.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import yuriy.dev.carfixbackend.auth.Role;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.mapper.UserMapper;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.repository.UserRepository;
import yuriy.dev.carfixbackend.token.JwtUtil;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private static final UUID USER_ID = UUID.randomUUID();

    private User buildUser() {
        return User.builder()
                .id(USER_ID)
                .username("user@mail.ru")
                .password("encoded")
                .firstName("Иван")
                .lastName("Иванов")
                .phoneNumber("+79990001122")
                .role(Role.ROLE_USER)
                .build();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addUser_savesUserAndReturnsDto() {
        UserDto input = new UserDto(null, "user@mail.ru", "pass", "+79990001122", "Иван", "Иванов", Role.ROLE_USER);
        User user = buildUser();
        UserDto savedDto = new UserDto(USER_ID, "user@mail.ru", "encoded", "+79990001122", "Иван", "Иванов", Role.ROLE_USER);

        when(userMapper.toUser(input)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(savedDto);

        UserDto result = userService.addUser(input);

        assertThat(result.id()).isEqualTo(USER_ID);
        assertThat(result.username()).isEqualTo("user@mail.ru");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_existingUser_updatesFieldsAndReturnsNewToken() {
        User user = buildUser();
        UserDto update = new UserDto(null, "new@mail.ru", null, "+79993334455", "Петр", "Петров", null);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("new-jwt-token");

        String token = userService.updateUser(USER_ID, update);

        assertThat(token).isEqualTo("new-jwt-token");
        assertThat(user.getUsername()).isEqualTo("new@mail.ru");
        assertThat(user.getFirstName()).isEqualTo("Петр");
        assertThat(user.getLastName()).isEqualTo("Петров");
        assertThat(user.getPhoneNumber()).isEqualTo("+79993334455");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_emptyFields_keepsOldValues() {
        User user = buildUser();
        UserDto update = new UserDto(null, "", null, "", "", "", null);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt");

        userService.updateUser(USER_ID, update);

        assertThat(user.getUsername()).isEqualTo("user@mail.ru");
        assertThat(user.getFirstName()).isEqualTo("Иван");
        assertThat(user.getLastName()).isEqualTo("Иванов");
        assertThat(user.getPhoneNumber()).isEqualTo("+79990001122");
    }

    @Test
    void updateUser_userNotFound_returnsNull() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        String result = userService.updateUser(USER_ID,
                new UserDto(null, "new@mail.ru", null, "+1", "А", "Б", null));

        assertThat(result).isNull();
    }

    @Test
    void deleteUser_delegatesToRepository() {
        userService.deleteUser(USER_ID);

        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void findById_returnsMappedDto() {
        User user = buildUser();
        UserDto dto = new UserDto(USER_ID, "user@mail.ru", "encoded", "+79990001122", "Иван", "Иванов", Role.ROLE_USER);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.findById(USER_ID);

        assertThat(result.username()).isEqualTo("user@mail.ru");
    }

    @Test
    void existsByUsername_delegatesToRepository() {
        when(userRepository.existsByUsername("user@mail.ru")).thenReturn(true);

        assertThat(userService.existsByUsername("user@mail.ru")).isTrue();
        assertThat(userService.existsByUsername("unknown@mail.ru")).isFalse();
    }

    @Test
    void getByUsername_returnsUser_whenExists() {
        User user = buildUser();
        when(userRepository.findByUsername("user@mail.ru")).thenReturn(Optional.of(user));

        assertThat(userService.getByUsername("user@mail.ru")).isEqualTo(user);
    }

    @Test
    void getByUsername_returnsNull_whenNotExists() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThat(userService.getByUsername("ghost@mail.ru")).isNull();
    }

    @Test
    void getCurrentUser_returnsUserFromSecurityContext() {
        User user = buildUser();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        assertThat(userService.getCurrentUser()).isEqualTo(user);
    }

    @Test
    void getCurrentUser_throws_whenPrincipalUnknown() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymous", null));

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
