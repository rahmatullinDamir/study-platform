package rahmatullin.dev.studyplatform.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rahmatullin.dev.studyplatform.dtos.ApiResponse;
import rahmatullin.dev.studyplatform.dtos.RegistrationForm;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.services.RegistrationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> registration(@RequestBody @Valid RegistrationForm registrationForm,
                                                             HttpServletResponse response) {
        UserDto userDto = registrationService.createUser(registrationForm, response);
        return ResponseEntity.ok(
                new ApiResponse<>("Успешная регистрация пользователя", userDto)
        );
    }
}
