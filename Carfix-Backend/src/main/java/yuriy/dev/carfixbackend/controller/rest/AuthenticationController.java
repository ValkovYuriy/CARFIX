package yuriy.dev.carfixbackend.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import yuriy.dev.carfixbackend.auth.AuthenticationService;
import yuriy.dev.carfixbackend.dto.JwtAuthenticationResponse;
import yuriy.dev.carfixbackend.dto.SignInRequest;
import yuriy.dev.carfixbackend.dto.SignUpRequest;


@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInRequest signInRequest) {
        JwtAuthenticationResponse jwt = authenticationService.signInJwt(signInRequest);
        return ResponseEntity.ok(jwt);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest signUpRequest) {
        JwtAuthenticationResponse jwt = authenticationService.signUpJwt(signUpRequest);
        return ResponseEntity.ok(jwt);
    }
}
