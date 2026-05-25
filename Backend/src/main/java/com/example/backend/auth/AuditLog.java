package com.example.backend.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "audit_logs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private String username;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "text")
    private String detail;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public AuditLog() {}

    public AuditLog(UUID userId, String username, String action, String detail) {
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.detail = detail;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
