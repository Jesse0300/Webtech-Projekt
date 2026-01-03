package htw.webtech.config;

import htw.webtech.business.service.JwtService;
import htw.webtech.persistence.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);

        try {
            String username = jwtService.getUsername(token);
            userRepository.findByUsername(username).ifPresent(user -> {
                var authentication = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        } catch (Exception ignored) {}

        filterChain.doFilter(request, response);
    }
}
