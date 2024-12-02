package com.example.backend.Appointment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.backend.Customer.Person;
import com.example.backend.Customer.Pet;

/**
 * Represents an appointment in the system.
 */
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long appointmentId; // JPA 主键，自动生成

    private LocalDate date;
    private LocalTime time;
    private String reason;
    private Person person; // The person who made the appointment
    private Pet pet; // The pet for whom the appointment is made
    private Status status;
    private Doctor doctor;

    public enum Status {
        PENDING,
        CANCELLED,
        COMPLETED
    }

    public enum Doctor {
        Clair,
        Michell,
        Jay,
        Alex,
        Cam
    }

    public Appointment() {
        // Default constructor for JPA
    }

    public Appointment(LocalDate date, Person person, Pet pet, LocalTime time, String reason, Doctor doctor) {
        this.date = date;
        this.person = person;
        this.pet = pet;
        this.time = time;
        this.reason = reason;
        this.status = Status.PENDING; // 默认状态
        this.doctor = doctor;
    }

    // Getters and Setters
    public long getAppointmentId() {
        return appointmentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", date=" + date +
                ", time=" + time +
                ", reason='" + reason + '\'' +
                ", person=" + person +
                ", pet=" + pet +
                ", status=" + status +
                ", doctor=" + doctor +
                '}';
    }
}

