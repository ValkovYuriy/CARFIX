package yuriy.dev.carfixbackend.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import yuriy.dev.carfixbackend.auth.AuthenticationService;
import yuriy.dev.carfixbackend.dto.request.SignInRequest;
import yuriy.dev.carfixbackend.dto.request.SignUpRequest;
import yuriy.dev.carfixbackend.dto.response.JwtAuthenticationResponse;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid SignInRequest signInRequest) {
        JwtAuthenticationResponse jwt = authenticationService.signInJwt(signInRequest);
        return ResponseEntity.ok(jwt);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid SignUpRequest signUpRequest) {
        JwtAuthenticationResponse jwt = authenticationService.signUpJwt(signUpRequest);
        return ResponseEntity.ok(jwt);
    }
}
