package rahmatullin.dev.studyplatform.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import rahmatullin.dev.studyplatform.exceptions.InvalidRefreshTokenException;
import rahmatullin.dev.studyplatform.models.RefreshToken;
import rahmatullin.dev.studyplatform.repositories.RefreshTokenRepository;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class JwtService {
    @Value("${jwt.access.expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final RefreshTokenRepository tokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public String generateRefreshToken(String email, Long userId) {
        return generateToken(email, userId, refreshExpiration);
    }

    public String generateAccessToken(String email, Long userId) {
        return generateToken(email, userId, accessExpiration);
    }

    private String generateToken(String email, Long userId, long expiration) {
        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }

    public String getEmail(String token) {
        return Optional.of(jwtVerifier.verify(token))
                .map(Payload::getSubject)
                .orElse(null);
    }

    public Long getUserId(String token) {
        return Optional.of(jwtVerifier.verify(token))
                .map(decodedJWT -> decodedJWT.getClaim("userId").asLong())
                .orElse(null);
    }

    public boolean validateToken(String token) {
        try {
            jwtVerifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void revokeRefreshToken(String username) {
        if (!tokenRepository.existsById(username)) {
            throw new InvalidRefreshTokenException("Refresh Token не найден для пользователя: " + username);
        }
        tokenRepository.deleteById(username);
    }

    public void equalsTokens(String email, String sentToken) {
        RefreshToken storedToken = tokenRepository.findById(email)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh Token не найден для пользователя: " + email));

        if (!sentToken.equals(storedToken.getToken())) {
            throw new InvalidRefreshTokenException("Недействительный Refresh Token для пользователя: " + email);
        }
    }

    public void saveRefreshToken(String username, String refreshToken) {
        RefreshToken token = new RefreshToken()
                .setUsername(username)
                .setToken(refreshToken)
                .setTimeToLiveSeconds(refreshExpiration);
        tokenRepository.save(token);

        redisTemplate.expire("refresh_token:" + username, token.getTimeToLiveSeconds(), TimeUnit.MILLISECONDS);
    }
}
