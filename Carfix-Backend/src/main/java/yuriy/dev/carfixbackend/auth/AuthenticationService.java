package yuriy.dev.carfixbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.SignUpRequest;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.mapper.UserMapper;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.service.UserService;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public boolean signUp(SignUpRequest request) {
        SecurityContextHolder.clearContext();
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        UserDto userDto = userMapper.toDto(user);
        if(userService.existsByUsername(user.getUsername())){
            return false;
        }
        userService.addUser(userDto);
        return true;
    }

}
