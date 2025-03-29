package yuriy.dev.carfixbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {

    @Size(min = 5, max = 50, message = "Адрес электронной почты должен содержать от 5 до 50 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Адрес электронной почты должен быть в формате user@example.com")
    private String username;

    @Size(max = 128, message = "Длина пароля должна быть не более 128 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
