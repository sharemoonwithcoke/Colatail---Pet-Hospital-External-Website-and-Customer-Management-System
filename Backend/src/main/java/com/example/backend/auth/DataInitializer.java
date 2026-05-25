package com.example.backend.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (!userRepository.existsByUsername("usermanage")) {
                SystemUser admin = new SystemUser();
                admin.setUsername("usermanage");
                admin.setPasswordHash(passwordEncoder.encode("usermanage"));
                admin.setRole(SystemUser.Role.ADMIN);
                admin.setMfaEnabled(false);
                admin.setActive(true);
                userRepository.save(admin);
                log.info("==> Default admin account created: username=usermanage");
            } else {
                log.info("==> Default admin account already exists, skipping seed");
            }
        } catch (Exception e) {
            log.error("==> Failed to seed admin account: {}", e.getMessage(), e);
        }
    }
}
