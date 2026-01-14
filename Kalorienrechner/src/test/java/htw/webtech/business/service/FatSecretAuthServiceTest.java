package htw.webtech.business.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;

class FatSecretAuthServiceTest {

    @Test
    void getAccessToken_returnsToken_whenResponseContainsAccessToken() {
        ExchangeFunction fx = req -> {
            String body = "{\"access_token\":\"abc123\"}";
            ClientResponse resp = ClientResponse.create(org.springframework.http.HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .build();
            return Mono.just(resp);
        };

        WebClient wc = WebClient.builder().exchangeFunction(fx).build();
        FatSecretAuthService svc = new FatSecretAuthService(wc);

        ReflectionTestUtils.setField(svc, "clientId", "id");
        ReflectionTestUtils.setField(svc, "clientSecret", "secret");

        String token = svc.getAccessToken();
        assertThat(token).isEqualTo("abc123");
    }

    @Test
    void getAccessToken_throws_whenAccessTokenMissing() {
        ExchangeFunction fx = req -> {
            String body = "{\"nope\":\"x\"}";
            ClientResponse resp = ClientResponse.create(org.springframework.http.HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .build();
            return Mono.just(resp);
        };

        WebClient wc = WebClient.builder().exchangeFunction(fx).build();
        FatSecretAuthService svc = new FatSecretAuthService(wc);

        ReflectionTestUtils.setField(svc, "clientId", "id");
        ReflectionTestUtils.setField(svc, "clientSecret", "secret");

        assertThatThrownBy(svc::getAccessToken)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("access_token");
    }
}
