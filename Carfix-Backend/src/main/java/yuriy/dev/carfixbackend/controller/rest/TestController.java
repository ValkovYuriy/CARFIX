package yuriy.dev.carfixbackend.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuriy.dev.carfixbackend.dto.EmailLetter;
import yuriy.dev.carfixbackend.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/send-email")
public class TestController {


    private final EmailService emailService;

    @PostMapping
    public void sendEmail(@RequestBody EmailLetter email) {
        emailService.sendEmailAsync(email.to(),email.subject(),email.text());
    }
}
