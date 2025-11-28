package rahmatullin.dev.studyplatform.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginForm {
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Неверный формат адреса электронной почты")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
