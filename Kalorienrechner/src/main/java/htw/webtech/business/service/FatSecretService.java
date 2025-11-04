package htw.webtech.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FatSecretService {

    private final WebClient fatsecretApiClient;
    private final FatSecretAuthService authService;

    public Map<String, Object> searchFoods(String query, int page, int pageSize) {
        String token = authService.getAccessToken();

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
    }
}