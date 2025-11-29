package rahmatullin.dev.studyplatform.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import rahmatullin.dev.studyplatform.models.enums.UserRoles;

@Data
@Accessors(chain = true)
public class RegistrationForm {
    @NotBlank(message = "Адрес электронной почты не может быть пустым")
    @Email(message = "Неверный формат адреса электронной почты")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 50, message = "Пароль должен содержать от 8 до 50 символов")
    @Pattern(regexp = "(?=.*[A-Z]).*", message = "Пароль должен содержать хотя бы одну заглавную букву")
    @Pattern(regexp = "(?=.*[a-z]).*", message = "Пароль должен содержать хотя бы одну строчную букву")
    @Pattern(regexp = "(?=.*\\d).*", message = "Пароль должен содержать хотя бы одну цифру")
    @Pattern(regexp = "(?=.*[@$!%*?&]).*", message = "Пароль должен содержать хотя бы один специальный символ")
    private String password;

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;
    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @NotNull(message = "Роль пользователя обязательна")
    private UserRoles role;
}
