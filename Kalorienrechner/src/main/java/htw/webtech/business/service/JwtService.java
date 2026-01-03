package htw.webtech.business.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final byte[] secret;

    public JwtService(@Value("${app.jwt.secret}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String createToken(Long userId, String username) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000L * 60 * 60 * 24 * 7))
                .signWith(Keys.hmacShaKeyFor(secret))
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
