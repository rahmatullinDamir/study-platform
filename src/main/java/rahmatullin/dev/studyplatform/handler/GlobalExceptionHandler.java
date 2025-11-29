package rahmatullin.dev.studyplatform.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rahmatullin.dev.studyplatform.dtos.ApiResponse;
import rahmatullin.dev.studyplatform.exceptions.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        logger.warn("Validation error: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "Ошибка валидации",
                errors
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUserAlreadyExistException(UserAlreadyExistException e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(EMAIL, e.getMessage());

        logger.warn("User already exists: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "Ошибка регистрации пользователя",
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleCourseAlreadyExistException(CourseAlreadyExistsException e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorDetails", e.getMessage());

        logger.warn("Course already exists: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "Ошибка создания курса",
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleCourseNotFoundException(CourseNotFoundException e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorDetails", e.getMessage());

        logger.warn("Course not found: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "Курс с таким id не найден",
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotPermitedToCourseException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleNotPermitedToCourseException(NotPermitedToCourseException e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("errorDetails", e.getMessage());

        logger.warn("Haven't permissions to this course: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "У вас нет прав для работы с этим курсом",
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handlePasswordMismatchException(PasswordMismatchException e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(PASSWORD, e.getMessage());
        logger.warn("Password mismatch: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "Ошибка аутентификации пользователя",
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUserNotFoundException(UserNotFoundException e) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put(PASSWORD, e.getMessage());
        logger.error("User not found: {}", e.getMessage());
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                "Ошибка идентификации пользователя",
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenExpiredException(TokenExpiredException e) {
        logger.warn("Token expired: {}", e.getMessage());
        ApiResponse<String> response = new ApiResponse<>(
                "Ошибка аутентификации пользователя",
                "Срок действия токена истек, обновите токен"
        );

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthMissing(
            AuthenticationCredentialsNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("Требуется аутентификация", null));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ApiResponse<String>> handleJWTVerificationException(JWTVerificationException e) {
        logger.error("JWT verification failed: {}", e.getMessage());
        ApiResponse<String> response = new ApiResponse<>(
                "Ошибка аутентификации пользователя",
                "Невалидный токен"
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        logger.error("Invalid refresh token: {}", e.getMessage());
        ApiResponse<String> response = new ApiResponse<>(
                "Ошибка аутентификации",
                e.getMessage()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
