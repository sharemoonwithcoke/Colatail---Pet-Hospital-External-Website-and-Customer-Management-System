package com.example.backend.Appointment;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.backend.Customer.Person;
import com.example.backend.Customer.Pet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private LocalDate date;
    private LocalTime time;
    private String reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    @JsonIgnoreProperties({"pets", "hibernateLazyInitializer", "handler"})
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    @JsonIgnoreProperties({"owner", "caseRecords", "reminders", "hibernateLazyInitializer", "handler"})
    private Pet pet;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Doctor doctor;

    public enum Status {
        PENDING, CANCELLED, COMPLETED
    }

    public enum Doctor {
        Clair, Michell, Jay, Alex, Cam
    }

    public Appointment() {}

    public Appointment(LocalDate date, Person person, Pet pet, LocalTime time, String reason, Doctor doctor) {
        this.date = date;
        this.person = person;
        this.pet = pet;
        this.time = time;
        this.reason = reason;
        this.status = Status.PENDING;
        this.doctor = doctor;
    }

    public Long getAppointmentId() { return appointmentId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}
