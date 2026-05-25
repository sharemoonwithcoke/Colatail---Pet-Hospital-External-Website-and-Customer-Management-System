package com.example.backend.Controller;

import com.example.backend.auth.*;
import com.example.backend.Customer.PersonRepository;
import com.example.backend.Customer.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PersonRepository personRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, AuditLogRepository auditLogRepository,
                           PersonRepository personRepository, PetRepository petRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.personRepository = personRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── User management ──

    @GetMapping("/users")
    public List<SystemUser> listUsers() { return userRepository.findAll(); }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {
        if (userRepository.existsByUsername(req.username)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        SystemUser user = new SystemUser();
        user.setUsername(req.username);
        user.setPasswordHash(passwordEncoder.encode(req.password));
        user.setRole(SystemUser.Role.STAFF);  // admins can only create staff accounts
        user.setActive(true);
        userRepository.save(user);
        audit("CREATE_STAFF", "Created staff user: " + req.username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            user.setRole(SystemUser.Role.valueOf(body.get("role").toUpperCase()));
            userRepository.save(user);
            audit("CHANGE_ROLE", "Changed role of " + user.getUsername() + " to " + body.get("role"));
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/active")
    public ResponseEntity<?> setActive(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        return userRepository.findById(id).map(user -> {
            user.setActive(body.get("active"));
            userRepository.save(user);
            audit("SET_ACTIVE", "Set active=" + body.get("active") + " for " + user.getUsername());
            return ResponseEntity.ok(user);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            user.setPasswordHash(passwordEncoder.encode(body.get("password")));
            userRepository.save(user);
            audit("RESET_PASSWORD", "Reset password for " + user.getUsername());
            return ResponseEntity.ok(Map.of("message", "Password reset"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();
        audit("DELETE_USER", "Deleted user: " + userOpt.get().getUsername());
        userRepository.delete(userOpt.get());
        return ResponseEntity.noContent().build();
    }

    // ── Customer management (admin-only deletes) ──

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        if (!personRepository.existsById(id)) return ResponseEntity.<Void>notFound().build();
        audit("DELETE_CUSTOMER", "Deleted customer id=" + id);
        personRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Audit logs ──

    @GetMapping("/audit-logs")
    public Page<AuditLog> getAuditLogs(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "50") int size) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    // ── Helpers ──

    private void audit(String action, String detail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "system";
        SystemUser actor = userRepository.findByUsername(username).orElse(null);
        UUID userId = actor != null ? actor.getId() : null;
        auditLogRepository.save(new AuditLog(userId, username, action, detail));
    }

    static class CreateUserRequest {
        public String username, password, role;
    }
}
