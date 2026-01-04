package htw.webtech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 🔓 öffentlich (ohne JWT erreichbar)
                        .requestMatchers(
                                "/api/v1/foods/**",
                                "/api/v1/categories/**",
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // 🔒 alles andere nur mit Auth
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
