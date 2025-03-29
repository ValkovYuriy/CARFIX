package yuriy.dev.carfixbackend.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.JwtAuthenticationResponse;
import yuriy.dev.carfixbackend.dto.SignInRequest;
import yuriy.dev.carfixbackend.dto.SignUpRequest;
import yuriy.dev.carfixbackend.dto.UserDto;
import yuriy.dev.carfixbackend.mapper.UserMapper;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.service.UserService;
import yuriy.dev.carfixbackend.token.JwtUtil;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUpJwt(SignUpRequest request) {
        var user =  User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        UserDto userDto = userMapper.toDto(user);
        userService.addUser(userDto);

        var jwt = jwtUtil.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signInJwt(SignInRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var userDetails = (UserDetails) authentication.getPrincipal();
        var jwt = jwtUtil.generateToken(userDetails);

        return new JwtAuthenticationResponse(jwt);
    }

}
