package htw.webtech.business.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FatSecretServiceTest {

    @Test
    void searchFoods_setsBearerToken_andClampsPageAndPageSize() {
        FatSecretAuthService auth = mock(FatSecretAuthService.class);
        when(auth.getAccessToken()).thenReturn("TOKEN123");

        final URI[] seenUri = new URI[1];
        final String[] authHeader = new String[1];

        ExchangeFunction fx = req -> {
            seenUri[0] = req.url();
            authHeader[0] = req.headers().getFirst("Authorization");

            String body = "{\"foods\":{\"food\":[]}}";
            ClientResponse resp = ClientResponse.create(org.springframework.http.HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .build();
            return Mono.just(resp);
        };

        WebClient wc = WebClient.builder()
                // Base URL egal, wir prüfen nur query params
                .baseUrl("https://example.com")
                .exchangeFunction(fx)
                .build();

        FatSecretService svc = new FatSecretService(wc, auth);

        Map<String, Object> out = svc.searchFoods("apple", -5, 999);

        assertThat(out).isNotNull();
        assertThat(authHeader[0]).isEqualTo("Bearer TOKEN123");

        // page_number muss auf 0 geklemmt werden, max_results auf 50
        String q = seenUri[0].getQuery();
        assertThat(q).contains("method=foods.search");
        assertThat(q).contains("search_expression=apple");
        assertThat(q).contains("page_number=0");
        assertThat(q).contains("max_results=50");
        assertThat(q).contains("format=json");

        verify(auth).getAccessToken();
    }
}
