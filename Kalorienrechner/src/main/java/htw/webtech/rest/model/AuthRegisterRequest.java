package htw.webtech.rest.model;

public record AuthRegisterRequest(
        String username,
        String email,
        String password
) {}
