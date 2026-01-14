package htw.webtech.business.service;

import htw.webtech.persistence.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;

class CurrentUserServiceTest {

    private final CurrentUserService service = new CurrentUserService();

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireUser_returnsUser_whenPrincipalIsUser_elseThrows401() {
        // success
        User u = new User();
        u.setId(1L);
        u.setUsername("test");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(u, "pw", java.util.List.of())
        );

        assertThat(service.requireUser().getId()).isEqualTo(1L);

        // fail: no auth
        SecurityContextHolder.clearContext();
        assertThatThrownBy(service::requireUser)
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                });
    }
}
