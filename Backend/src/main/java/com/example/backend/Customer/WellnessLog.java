package com.example.backend.Customer;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "wellness_logs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WellnessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonIgnoreProperties({"owner", "caseRecords", "hibernateLazyInitializer", "handler"})
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogType type;

    private String value;

    private LocalDateTime loggedAt;

    @Column(columnDefinition = "text")
    private String notes;

    @PrePersist
    protected void onCreate() { if (loggedAt == null) loggedAt = LocalDateTime.now(); }

    public enum LogType { WEIGHT, VACCINE, DEWORMING, OTHER }

    public UUID getId() { return id; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public LogType getType() { return type; }
    public void setType(LogType type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
