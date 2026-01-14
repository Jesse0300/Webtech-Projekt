package htw.webtech.rest.controller;

import htw.webtech.business.service.JwtService;
import htw.webtech.persistence.entity.User;
import htw.webtech.persistence.repository.UserRepository;
import htw.webtech.rest.model.AuthLoginRequest;
import htw.webtech.rest.model.AuthRegisterRequest;
import htw.webtech.rest.model.AuthResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }
        if (userRepository.findByUsername(req.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already in use");
        }

        User user = new User();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));

        user = userRepository.save(user);

        String token = jwtService.createToken(user.getId(), user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest req) {
        User user = userRepository.findByEmail(req.login())
                .or(() -> userRepository.findByUsername(req.login()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.createToken(user.getId(), user.getUsername());
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }
}
