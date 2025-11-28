package rahmatullin.dev.studyplatform.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rahmatullin.dev.studyplatform.dtos.LoginForm;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.exceptions.PasswordMismatchException;
import rahmatullin.dev.studyplatform.exceptions.UserNotFoundException;
import rahmatullin.dev.studyplatform.mapper.UserMapper;
import rahmatullin.dev.studyplatform.models.User;
import rahmatullin.dev.studyplatform.repositories.UserRepository;


import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto login(LoginForm loginForm, HttpServletResponse response) {
        String email = loginForm.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Неверное имя пользователя или пароль"));

        if (!passwordEncoder.matches(loginForm.getPassword(), user.getHashPassword())) {
            throw new PasswordMismatchException("Неверное имя пользователя или пароль");
        }

        tokenService.setTokens(email, response);

        return userMapper.mapToUserDto(user);
    }

    public Optional<UserDto> checkAuth(Optional<String> header) {
        if (header.isPresent()) {
            Long userId = Long.parseLong(header.get());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
            UserDto userDto = userMapper.mapToUserDto(user);
            return Optional.of(userDto);
        }
        return Optional.empty();
    }
}
