package com.example.backend.Controller;

import com.example.backend.doctor.Doctor;
import com.example.backend.doctor.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<Doctor> getAll(@RequestParam(required = false) boolean activeOnly) {
        if (activeOnly) return doctorRepository.findByIsActiveTrue();
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable UUID id) {
        return doctorRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorRepository.save(doctor));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Doctor> update(@PathVariable UUID id, @RequestBody Doctor updated) {
        return doctorRepository.findById(id).map(doc -> {
            doc.setName(updated.getName());
            doc.setSpecialty(updated.getSpecialty());
            doc.setActive(updated.isActive());
            return ResponseEntity.ok(doctorRepository.save(doc));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!doctorRepository.existsById(id)) return ResponseEntity.notFound().build();
        doctorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
