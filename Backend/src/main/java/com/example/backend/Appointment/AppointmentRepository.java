package com.example.backend.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByStatus(Appointment.Status status);
    List<Appointment> findByPerson_Id(UUID personId);
    List<Appointment> findByPet_Id(UUID petId);
    List<Appointment> findByDoctor_Id(UUID doctorId);
    List<Appointment> findByScheduledTimeBetweenOrderByScheduledTimeAsc(LocalDateTime start, LocalDateTime end);
}
