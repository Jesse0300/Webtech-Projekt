package htw.webtech.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FatSecretService {

    private final WebClient fatsecretApiClient;
    private final FatSecretAuthService authService;

    public Map<String, Object> searchFoods(String query, int page, int pageSize) {
        if (query == null || query.isBlank()) {
            return Map.of("foods", Map.of("food", java.util.List.of()));
        }

        String token = authService.getAccessToken();

        try {
            return fatsecretApiClient.get()
                    .uri(uri -> uri
                            .queryParam("method", "foods.search")
                            .queryParam("search_expression", query)
                            .queryParam("page_number", Math.max(0, page))
                            .queryParam("max_results", Math.min(50, Math.max(1, pageSize)))
                            .queryParam("format", "json")
                            .build())
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        } catch (WebClientResponseException e) {
            return Map.of(
                    "error", Map.of(
                            "message", "FatSecret API Fehler: " + e.getStatusCode() + " " + safeBody(e)
                    )
            );
        } catch (Exception e) {
            return Map.of(
                    "error", Map.of(
                            "message", "FatSecret Fehler: " + (e.getMessage() != null ? e.getMessage() : e.toString())
                    )
            );
        }
    }

    private String safeBody(WebClientResponseException e) {
        try {
            String b = e.getResponseBodyAsString();
            if (b == null) return "";
            return b.length() > 500 ? b.substring(0, 500) + "…" : b;
        } catch (Exception ex) {
            return "";
        }
    }
}
