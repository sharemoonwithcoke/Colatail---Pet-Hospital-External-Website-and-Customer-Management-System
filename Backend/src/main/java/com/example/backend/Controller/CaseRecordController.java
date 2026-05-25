package com.example.backend.Controller;

import com.example.backend.Appointment.AppointmentRepository;
import com.example.backend.Customer.CaseRecord;
import com.example.backend.Customer.CaseRecordRepository;
import com.example.backend.Customer.PetRepository;
import com.example.backend.doctor.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/case-records")
public class CaseRecordController {

    private final CaseRecordRepository caseRecordRepository;
    private final PetRepository petRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public CaseRecordController(CaseRecordRepository caseRecordRepository, PetRepository petRepository,
                                DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.caseRecordRepository = caseRecordRepository;
        this.petRepository = petRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/pet/{petId}")
    public List<CaseRecord> getByPet(@PathVariable UUID petId) {
        return caseRecordRepository.findByPet_IdOrderByVisitedAtDesc(petId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseRecord> getById(@PathVariable UUID id) {
        return caseRecordRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CaseRecordRequest req) {
        var pet = petRepository.findById(req.petId).orElse(null);
        var doctor = req.doctorId != null ? doctorRepository.findById(req.doctorId).orElse(null) : null;
        if (pet == null) return ResponseEntity.badRequest().body("Pet not found");
        if (doctor == null) return ResponseEntity.badRequest().body("Doctor not found");

        CaseRecord record = new CaseRecord();
        record.setPet(pet);
        record.setDoctor(doctor);
        record.setChiefComplaint(req.chiefComplaint);
        record.setDiagnosis(req.diagnosis);
        record.setPrescription(req.prescription);
        record.setNotes(req.notes);
        if (req.appointmentId != null) appointmentRepository.findById(req.appointmentId).ifPresent(record::setAppointment);
        return ResponseEntity.ok(caseRecordRepository.save(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody CaseRecordRequest req) {
        return caseRecordRepository.findById(id).map(record -> {
            record.setChiefComplaint(req.chiefComplaint);
            record.setDiagnosis(req.diagnosis);
            record.setPrescription(req.prescription);
            record.setNotes(req.notes);
            if (req.doctorId != null) doctorRepository.findById(req.doctorId).ifPresent(record::setDoctor);
            return ResponseEntity.ok(caseRecordRepository.save(record));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!caseRecordRepository.existsById(id)) return ResponseEntity.notFound().build();
        caseRecordRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    static class CaseRecordRequest {
        public UUID petId, doctorId, appointmentId;
        public String chiefComplaint, diagnosis, prescription, notes;
    }
}
