package zw.ac.uz.dpdms.auth.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import zw.ac.uz.dpdms.auth.repo.UserRepository;
import zw.ac.uz.dpdms.auth.security.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return repo.findByUsername(req.username())
            .filter(u -> u.isEnabled() && encoder.matches(req.password(), u.getPasswordHash()))
            .<ResponseEntity<?>>map(u -> ResponseEntity.ok(Map.of(
                "token", jwt.issue(u),
                "role", u.getRole().name(),
                "ward", u.getWard() == null ? "" : u.getWard())))
            .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}