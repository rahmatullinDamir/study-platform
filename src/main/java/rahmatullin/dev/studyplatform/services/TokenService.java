package rahmatullin.dev.studyplatform.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rahmatullin.dev.studyplatform.dtos.ApiResponse;
import rahmatullin.dev.studyplatform.dtos.TokenResponse;
import rahmatullin.dev.studyplatform.dtos.UserDto;
import rahmatullin.dev.studyplatform.exceptions.InvalidRefreshTokenException;
import rahmatullin.dev.studyplatform.exceptions.UserNotFoundException;
import rahmatullin.dev.studyplatform.mapper.UserMapper;
import rahmatullin.dev.studyplatform.models.User;
import rahmatullin.dev.studyplatform.models.enums.UserRoles;
import rahmatullin.dev.studyplatform.repositories.UserRepository;
import rahmatullin.dev.studyplatform.security.service.JwtService;


import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ApiResponse<TokenResponse> refreshTokens(String refreshToken) {
        String email = jwtService.getEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
        jwtService.equalsTokens(email, refreshToken);

        Long userId = userRepository.findIdByEmail(email);
        UserRoles role = user.getRole();

        String newAccessToken = jwtService.generateAccessToken(email, userId, role);
        String newRefreshToken = jwtService.generateRefreshToken(email, userId, role);

        jwtService.saveRefreshToken(email, newRefreshToken);

        return new ApiResponse<>("Новые токены авторизации",
                new TokenResponse(newAccessToken, newRefreshToken));
    }

    public void setTokens(String email, HttpServletResponse response) {
        Long userId = userRepository.findIdByEmail(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
        UserRoles role = user.getRole();
        String accessToken = jwtService.generateAccessToken(email, userId, role);
        String refreshToken = jwtService.generateRefreshToken(email, userId, role);

        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        jwtService.saveRefreshToken(email, refreshToken);
    }

    public UserDto revokeTokens(HttpServletRequest request, HttpServletResponse response) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Такой пользователь не найден"));
        String email = user.getEmail();
        jwtService.revokeRefreshToken(email);
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(cookie -> "refresh_token".equals(cookie.getName()) || "access_token".equals(cookie.getName()))
                    .forEach(cookie -> {
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    });
        } else {
            throw new InvalidRefreshTokenException("Токены не найдены");
        }

        return userMapper.mapToUserDto(user);
    }
}
