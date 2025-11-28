package rahmatullin.dev.studyplatform.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rahmatullin.dev.studyplatform.dtos.ApiResponse;
import rahmatullin.dev.studyplatform.dtos.LoginForm;
import rahmatullin.dev.studyplatform.dtos.TokenResponse;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.services.LoginService;
import rahmatullin.dev.studyplatform.services.TokenService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {
    private final LoginService loginService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@Valid @RequestBody LoginForm loginForm, HttpServletResponse response) {
        UserDto userDto = loginService.login(loginForm, response);
        ApiResponse<UserDto> apiResponse = new ApiResponse<>("Успешная авторизация", userDto);
        return ResponseEntity.ok(apiResponse);
    }

    @Hidden
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshTokens(@RequestParam("refreshToken") String refreshToken) {
        ApiResponse<TokenResponse> apiResponse = tokenService.refreshTokens(refreshToken);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Optional<UserDto>>> checkAuth(HttpServletRequest request) {
        Optional<String> userIdOptional = Optional.ofNullable(request.getHeader("X-User-Id"));
        Optional<UserDto> optionalUserDto = loginService.checkAuth(userIdOptional);
        if (optionalUserDto.isEmpty()) {
            ApiResponse<Optional<UserDto>> apiResponse = new ApiResponse<>("Пользователь не авторизован", optionalUserDto);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }
        ApiResponse<Optional<UserDto>> apiResponse = new ApiResponse<>("Пользователь авторизован", optionalUserDto);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<UserDto>> logout(HttpServletRequest request, HttpServletResponse response) {
        UserDto userDto = tokenService.revokeTokens(request, response);
        ApiResponse<UserDto> apiResponse = new ApiResponse<>("Успешный выход", userDto);
        return ResponseEntity.ok(apiResponse);
    }
}
