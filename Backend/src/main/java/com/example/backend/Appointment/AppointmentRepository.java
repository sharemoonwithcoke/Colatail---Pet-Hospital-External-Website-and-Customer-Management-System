package com.example.backend.Appointment;



import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    /**
     * Find appointments by status
     * @param status Appointment status (e.g., PENDING, CANCELLED, COMPLETED)
     * @return List of matching appointments
     */
    List<Appointment> findByStatus(Appointment.Status status);

    /**
     * Find appointments by a specific date
     * @param date The specified date
     * @return List of matching appointments
     */
    List<Appointment> findByDate(LocalDate date);

    /**
     * Find appointments by doctor
     * @param doctor The specified doctor
     * @return List of matching appointments
     */
    List<Appointment> findByDoctor(Appointment.Doctor doctor);

    /**
     * Find appointments by person ID
     * @param personId The ID of the person
     * @return List of matching appointments
     */
    List<Appointment> findByPerson_PersonId(Long personId);

    /**
     * Find appointments by pet ID
     * @param petId The ID of the pet
     * @return List of matching appointments
     */
    List<Appointment> findByPet_PetId(Long petId);

    /**
     * Find appointments within a date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of matching appointments
     */
    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
