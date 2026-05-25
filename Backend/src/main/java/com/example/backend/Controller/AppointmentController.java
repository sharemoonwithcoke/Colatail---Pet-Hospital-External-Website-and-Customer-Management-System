package com.example.backend.Controller;

import com.example.backend.Appointment.Appointment;
import com.example.backend.Appointment.AppointmentRepository;
import com.example.backend.Customer.PersonRepository;
import com.example.backend.Customer.PetRepository;
import com.example.backend.doctor.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PersonRepository personRepository;
    private final PetRepository petRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentController(AppointmentRepository appointmentRepository,
                                 PersonRepository personRepository,
                                 PetRepository petRepository,
                                 DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.personRepository = personRepository;
        this.petRepository = petRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<Appointment> getAll(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) UUID personId) {
        if (personId != null) {
            List<Appointment> list = appointmentRepository.findByPerson_Id(personId);
            if (status != null && !status.isBlank()) {
                try {
                    Appointment.Status s = Appointment.Status.valueOf(status.toUpperCase());
                    list = list.stream().filter(a -> a.getStatus() == s).toList();
                } catch (IllegalArgumentException ignored) {}
            }
            return list;
        }
        if (status != null && !status.isBlank()) {
            try { return appointmentRepository.findByStatus(Appointment.Status.valueOf(status.toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }
        return appointmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable UUID id) {
        return appointmentRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AppointmentRequest req) {
        var person = personRepository.findById(req.personId).orElse(null);
        var pet = petRepository.findById(req.petId).orElse(null);
        if (person == null || pet == null) return ResponseEntity.badRequest().body("Person or pet not found");

        Appointment appt = new Appointment();
        appt.setPerson(person);
        appt.setPet(pet);
        appt.setScheduledTime(req.scheduledTime);
        appt.setNotes(req.notes);
        if (req.status != null) appt.setStatus(Appointment.Status.valueOf(req.status.toUpperCase()));
        if (req.doctorId != null) doctorRepository.findById(req.doctorId).ifPresent(appt::setDoctor);
        return ResponseEntity.ok(appointmentRepository.save(appt));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody AppointmentRequest req) {
        return appointmentRepository.findById(id).map(appt -> {
            appt.setScheduledTime(req.scheduledTime);
            appt.setNotes(req.notes);
            if (req.status != null) appt.setStatus(Appointment.Status.valueOf(req.status.toUpperCase()));
            if (req.doctorId != null) doctorRepository.findById(req.doctorId).ifPresent(appt::setDoctor);
            else appt.setDoctor(null);
            if (req.personId != null) personRepository.findById(req.personId).ifPresent(appt::setPerson);
            if (req.petId != null) petRepository.findById(req.petId).ifPresent(appt::setPet);
            return ResponseEntity.ok(appointmentRepository.save(appt));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!appointmentRepository.existsById(id)) return ResponseEntity.notFound().build();
        appointmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    static class AppointmentRequest {
        public UUID personId, petId, doctorId;
        public LocalDateTime scheduledTime;
        public String status, notes;
    }
}
