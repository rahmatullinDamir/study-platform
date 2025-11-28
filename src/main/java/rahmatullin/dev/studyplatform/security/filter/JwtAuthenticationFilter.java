package rahmatullin.dev.studyplatform.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rahmatullin.dev.studyplatform.security.service.JwtService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        String accessToken = extractTokenFromCookie(request, "access_token");
        
        if (accessToken != null && jwtService.validateToken(accessToken)) {
            try {
                Long userId = jwtService.getUserId(accessToken);
                String email = jwtService.getEmail(accessToken);
                
                if (userId != null && email != null) {
                    // Создаем wrapper для добавления заголовка X-User-Id
                    JwtRequestWrapper wrappedRequest = new JwtRequestWrapper(request);
                    wrappedRequest.addHeader("X-User-Id", userId.toString());
                    
                    // Устанавливаем аутентификацию в SecurityContext
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    filterChain.doFilter(wrappedRequest, response);
                    return;
                }
            } catch (Exception e) {
                // Если токен невалидный, просто пропускаем запрос без аутентификации
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

