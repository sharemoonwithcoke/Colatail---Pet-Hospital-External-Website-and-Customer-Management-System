package com.example.backend.Customer;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import com.example.backend.doctor.Doctor;
import com.example.backend.Appointment.Appointment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "case_records")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CaseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonIgnoreProperties({"owner", "caseRecords", "hibernateLazyInitializer", "handler"})
    private Pet pet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    @JsonIgnoreProperties({"person", "pet", "doctor", "hibernateLazyInitializer", "handler"})
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Doctor doctor;

    @Column(columnDefinition = "text")
    private String chiefComplaint;

    @Column(columnDefinition = "text")
    private String diagnosis;

    @Column(columnDefinition = "text")
    private String prescription;

    @Column(columnDefinition = "text")
    private String notes;

    private LocalDateTime visitedAt;

    @PrePersist
    protected void onCreate() { if (visitedAt == null) visitedAt = LocalDateTime.now(); }

    public UUID getId() { return id; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }
}
