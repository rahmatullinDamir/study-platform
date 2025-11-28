package rahmatullin.dev.studyplatform.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.exceptions.UserNotFoundException;
import rahmatullin.dev.studyplatform.mapper.UserMapper;
import rahmatullin.dev.studyplatform.models.User;
import rahmatullin.dev.studyplatform.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUserData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
        return userMapper.mapToUserDto(user);
    }
}
