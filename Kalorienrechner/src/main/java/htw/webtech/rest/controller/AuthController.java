package htw.webtech.rest.controller;

import htw.webtech.business.service.JwtService;
import htw.webtech.persistence.entity.User;
import htw.webtech.persistence.repository.UserRepository;
import htw.webtech.rest.model.AuthLoginRequest;
import htw.webtech.rest.model.AuthRegisterRequest;
import htw.webtech.rest.model.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRegisterRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new IllegalStateException("Email exists");

        if (userRepository.existsByUsername(req.username()))
            throw new IllegalStateException("Username exists");

        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .build();

        user = userRepository.save(user);
        String token = jwtService.createToken(user.getId(), user.getUsername());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthLoginRequest req) {
        User user = userRepository.findByEmail(req.login())
                .or(() -> userRepository.findByUsername(req.login()))
                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new IllegalStateException("Invalid credentials");

        String token = jwtService.createToken(user.getId(), user.getUsername());

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token
        );
    }
}
