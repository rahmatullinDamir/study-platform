package rahmatullin.dev.studyplatform.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rahmatullin.dev.studyplatform.dtos.RegistrationForm;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.exceptions.UserAlreadyExistException;
import rahmatullin.dev.studyplatform.mapper.UserMapper;
import rahmatullin.dev.studyplatform.models.User;
import rahmatullin.dev.studyplatform.repositories.UserRepository;


@RequiredArgsConstructor
@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserDto createUser(RegistrationForm registrationForm, HttpServletResponse response) {
        userRepository.findByEmail(registrationForm.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistException("Такой пользователь уже существует");
                });

        String hashPassword = passwordEncoder.encode(registrationForm.getPassword());
        User user = userMapper.mapToUser(registrationForm, hashPassword);
        userRepository.save(user);

        tokenService.setTokens(user.getEmail(), response);

        return userMapper.mapToUserDto(user);
    }
}
