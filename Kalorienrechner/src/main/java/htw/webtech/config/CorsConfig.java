package htw.webtech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override public void addCorsMappings(CorsRegistry reg) {
                reg.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:5173",
                                "https://kalorienrechner-frontend.onrender.com"
                        ) // Vite
                        .allowedMethods("GET","POST","PUT","DELETE");
            }
        };
    }
}