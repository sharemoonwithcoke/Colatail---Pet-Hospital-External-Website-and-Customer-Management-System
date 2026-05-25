package com.example.backend.Controller;

import com.example.backend.Appointment.Appointment;
import com.example.backend.Appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public List<Appointment> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String doctor) {
        if (status != null && !status.isBlank()) {
            try { return appointmentRepository.findByStatus(Appointment.Status.valueOf(status.toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }
        if (date != null && !date.isBlank()) {
            try { return appointmentRepository.findByDate(LocalDate.parse(date)); }
            catch (Exception ignored) {}
        }
        if (doctor != null && !doctor.isBlank()) {
            try { return appointmentRepository.findByDoctor(Appointment.Doctor.valueOf(doctor)); }
            catch (IllegalArgumentException ignored) {}
        }
        return appointmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable Long id) {
        return appointmentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody Appointment appointment) {
        if (appointment.getStatus() == null) appointment.setStatus(Appointment.Status.PENDING);
        return ResponseEntity.ok(appointmentRepository.save(appointment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> update(@PathVariable Long id, @RequestBody Appointment updated) {
        return appointmentRepository.findById(id).map(appt -> {
            appt.setDate(updated.getDate());
            appt.setTime(updated.getTime());
            appt.setReason(updated.getReason());
            appt.setStatus(updated.getStatus());
            appt.setDoctor(updated.getDoctor());
            if (updated.getPerson() != null) appt.setPerson(updated.getPerson());
            if (updated.getPet() != null) appt.setPet(updated.getPet());
            return ResponseEntity.ok(appointmentRepository.save(appt));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!appointmentRepository.existsById(id)) return ResponseEntity.notFound().build();
        appointmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
