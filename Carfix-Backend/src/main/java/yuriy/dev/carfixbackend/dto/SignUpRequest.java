package yuriy.dev.carfixbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class SignUpRequest {

    @Size(min = 5, max = 50, message = "Адрес электронной почты должен содержать от 5 до 50 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Адрес электронной почты должен быть в формате user@example.com")
    private String username;

    @Size(max = 128, message = "Длина пароля должна быть не более 128 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @Size(min = 3, max = 12 , message = "Номер телефона должен содержать от 3 до 12 символов")
    private String phoneNumber;

    @Size(min = 2, max = 100 , message = "Имя должно содержать от 2 до 100 символов")
    private String firstName;

    @Size(min = 2, max = 100 , message = "Фамилия должна содержать от 2 до 100 символов")
    private String lastName;
}
