package htw.webtech.rest.model;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(
        @NotBlank String login,   // username ODER email
        @NotBlank String password
) {}
