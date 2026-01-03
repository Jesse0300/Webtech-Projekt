package htw.webtech.rest.model;

public record AuthResponse(
        Long userId,
        String username,
        String email,
        String token
) {}