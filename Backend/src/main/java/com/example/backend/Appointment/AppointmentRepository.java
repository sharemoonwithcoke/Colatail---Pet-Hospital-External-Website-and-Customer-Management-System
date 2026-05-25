package com.example.backend.Appointment;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStatus(Appointment.Status status);
    List<Appointment> findByDate(LocalDate date);
    List<Appointment> findByDoctor(Appointment.Doctor doctor);
    List<Appointment> findByPerson_Id(Long personId);
    List<Appointment> findByPet_Id(Long petId);
    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
