package htw.webtech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpConfig {

    @Bean(name = "fatsecretAuthClient")
    public WebClient fatsecretAuthClient() {
        return WebClient.builder()
                .baseUrl("https://oauth.fatsecret.com")
                .build();
    }

    @Bean(name = "fatsecretApiClient")
    public WebClient fatsecretApiClient() {
        return WebClient.builder()
                .baseUrl("https://platform.fatsecret.com/rest/server.api")
                .build();
    }
}
