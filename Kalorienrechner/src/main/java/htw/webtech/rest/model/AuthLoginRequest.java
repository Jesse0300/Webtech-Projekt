package htw.webtech.rest.model;

public record AuthLoginRequest(
        String login,   // username ODER email
        String password
) {}