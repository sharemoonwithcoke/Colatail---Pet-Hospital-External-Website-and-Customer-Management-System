package com.example.backend.Controller;

import com.example.backend.Customer.CaseRecord;
import com.example.backend.Customer.CaseRecordRepository;
import com.example.backend.Customer.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/case-records")
public class CaseRecordController {

    private final CaseRecordRepository caseRecordRepository;
    private final PetRepository petRepository;

    @Autowired
    public CaseRecordController(CaseRecordRepository caseRecordRepository, PetRepository petRepository) {
        this.caseRecordRepository = caseRecordRepository;
        this.petRepository = petRepository;
    }

    @GetMapping("/pet/{petId}")
    public List<CaseRecord> getByPet(@PathVariable Long petId) {
        return caseRecordRepository.findByPet_IdOrderByDateDesc(petId);
    }

    @PostMapping
    public ResponseEntity<CaseRecord> create(@RequestBody CaseRecordRequest req) {
        return petRepository.findById(req.petId).map(pet -> {
            CaseRecord record = new CaseRecord(req.date, req.description, pet);
            return ResponseEntity.ok(caseRecordRepository.save(record));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!caseRecordRepository.existsById(id)) return ResponseEntity.notFound().build();
        caseRecordRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    static class CaseRecordRequest {
        public LocalDate date;
        public String description;
        public Long petId;
    }
}
