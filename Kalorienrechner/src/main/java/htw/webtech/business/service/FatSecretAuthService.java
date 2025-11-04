package htw.webtech.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FatSecretAuthService {

    private final WebClient fatsecretAuthClient;

    @Value("${fatsecret.client-id}")
    private String clientId;

    @Value("${fatsecret.client-secret}")
    private String clientSecret;

    public String getAccessToken() {
        Map<?, ?> resp = fatsecretAuthClient.post()
                .uri("/connect/token")
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&scope=basic")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Object token = resp != null ? resp.get("access_token") : null;
        if (token == null) {
            throw new IllegalStateException("FatSecret: access_token missing in response");
        }
        return token.toString();
    }
}
