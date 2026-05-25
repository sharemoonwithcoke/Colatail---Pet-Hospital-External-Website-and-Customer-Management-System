package com.example.backend.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TotpUtil totpUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil, TotpUtil totpUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.totpUtil = totpUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        SystemUser user = userRepository.findByUsername(req.username).orElse(null);
        if (user == null) {
            log.warn("Login failed: user '{}' not found", req.username);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        if (!user.isActive()) {
            log.warn("Login failed: user '{}' is inactive", req.username);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        if (!passwordEncoder.matches(req.password, user.getPasswordHash())) {
            log.warn("Login failed: wrong password for user '{}'", req.username);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        if (user.isMfaEnabled()) {
            if (req.totpCode == null) {
                return ResponseEntity.status(206).body(Map.of("mfaRequired", true));
            }
            if (!totpUtil.verify(user.getMfaSecret(), req.totpCode)) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid MFA code"));
            }
        }
        return ResponseEntity.ok(Map.of(
                "token", jwtUtil.generate(user),
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "mfaEnabled", user.isMfaEnabled()
        ));
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<?> setupMfa() {
        SystemUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (user.isMfaEnabled()) return ResponseEntity.badRequest().body(Map.of("error", "MFA already enabled"));
        String secret = totpUtil.generateSecret();
        user.setMfaSecret(secret);
        userRepository.save(user);
        String uri = totpUtil.getTotpUri(secret, user.getUsername(), "Colatail");
        return ResponseEntity.ok(Map.of("secret", secret, "otpauthUri", uri));
    }

    @PostMapping("/mfa/confirm")
    public ResponseEntity<?> confirmMfa(@RequestBody Map<String, Integer> body) {
        SystemUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        Integer code = body.get("code");
        if (code == null || user.getMfaSecret() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Setup MFA first"));
        }
        if (!totpUtil.verify(user.getMfaSecret(), code)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid code"));
        }
        user.setMfaEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "MFA enabled"));
    }

    @PostMapping("/mfa/disable")
    public ResponseEntity<?> disableMfa(@RequestBody Map<String, Integer> body) {
        SystemUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        Integer code = body.get("code");
        if (code == null || !user.isMfaEnabled()) {
            return ResponseEntity.badRequest().body(Map.of("error", "MFA not enabled"));
        }
        if (!totpUtil.verify(user.getMfaSecret(), code)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid code"));
        }
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "MFA disabled"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        SystemUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        if (!passwordEncoder.matches(req.currentPassword, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Current password incorrect"));
        }
        user.setPasswordHash(passwordEncoder.encode(req.newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password changed"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        SystemUser user = currentUser();
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name(),
                "mfaEnabled", user.isMfaEnabled()
        ));
    }

    private SystemUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    static class LoginRequest {
        public String username;
        public String password;
        public Integer totpCode;
    }

    static class ChangePasswordRequest {
        public String currentPassword;
        public String newPassword;
    }
}
