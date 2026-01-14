package htw.webtech.business.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    @Test
    void createToken_and_getUsername_roundtrip() {
        // secret muss für HS256 lang genug sein (mind. 32 bytes)
        JwtService jwt = new JwtService("0123456789abcdef0123456789abcdef");

        String token = jwt.createToken(1L, "jesse");
        String username = jwt.getUsername(token);

        assertThat(username).isEqualTo("jesse");
        assertThat(token).isNotBlank();
    }

    @Test
    void getUsername_throws_onInvalidToken() {
        JwtService jwt = new JwtService("0123456789abcdef0123456789abcdef");

        assertThatThrownBy(() -> jwt.getUsername("not-a-jwt"))
                .isInstanceOf(Exception.class);
    }
}
