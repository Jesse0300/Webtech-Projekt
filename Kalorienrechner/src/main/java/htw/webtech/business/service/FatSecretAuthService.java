package htw.webtech.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FatSecretAuthService {

    private final WebClient fatsecretAuthClient;

    @Value("${fatsecret.client-id:}")
    private String clientId;

    @Value("${fatsecret.client-secret:}")
    private String clientSecret;

    private volatile String cachedToken;
    private volatile long cachedTokenExpiresAtEpochSec = 0;

    public synchronized String getAccessToken() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException(
                    "FatSecret Credentials fehlen. Bitte ENV setzen: FATSECRET_CLIENT_ID und FATSECRET_CLIENT_SECRET"
            );
        }

        long now = Instant.now().getEpochSecond();
        if (cachedToken != null && now < (cachedTokenExpiresAtEpochSec - 30)) {
            return cachedToken;
        }

        Map<String, Object> resp = fatsecretAuthClient.post()
                .uri("/connect/token")
                .headers(h -> h.setBasicAuth(clientId, clientSecret))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("scope", "basic"))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Object token = resp != null ? resp.get("access_token") : null;
        if (token == null) {
            throw new IllegalStateException("FatSecret: access_token fehlt in der Token-Response");
        }

        long expiresIn = 3600;
        Object expiresRaw = resp.get("expires_in");
        if (expiresRaw != null) {
            try {
                expiresIn = Long.parseLong(String.valueOf(expiresRaw));
            } catch (Exception ignored) {}
        }

        cachedToken = token.toString();
        cachedTokenExpiresAtEpochSec = now + expiresIn;
        return cachedToken;
    }
}
