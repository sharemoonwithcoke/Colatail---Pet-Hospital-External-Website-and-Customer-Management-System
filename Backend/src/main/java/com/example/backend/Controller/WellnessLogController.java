package com.example.backend.Controller;

import com.example.backend.Customer.WellnessLog;
import com.example.backend.Customer.WellnessLogRepository;
import com.example.backend.Customer.PetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wellness-logs")
public class WellnessLogController {

    private final WellnessLogRepository wellnessLogRepository;
    private final PetRepository petRepository;

    public WellnessLogController(WellnessLogRepository wellnessLogRepository, PetRepository petRepository) {
        this.wellnessLogRepository = wellnessLogRepository;
        this.petRepository = petRepository;
    }

    @GetMapping("/pet/{petId}")
    public List<WellnessLog> getByPet(@PathVariable UUID petId) {
        return wellnessLogRepository.findByPet_IdOrderByLoggedAtDesc(petId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WellnessLogRequest req) {
        var pet = petRepository.findById(req.petId).orElse(null);
        if (pet == null) return ResponseEntity.badRequest().body("Pet not found");

        WellnessLog log = new WellnessLog();
        log.setPet(pet);
        log.setType(WellnessLog.LogType.valueOf(req.type.toUpperCase()));
        log.setValue(req.value);
        log.setNotes(req.notes);
        if (req.loggedAt != null) log.setLoggedAt(req.loggedAt);
        return ResponseEntity.ok(wellnessLogRepository.save(log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!wellnessLogRepository.existsById(id)) return ResponseEntity.notFound().build();
        wellnessLogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    static class WellnessLogRequest {
        public UUID petId;
        public String type, value, notes;
        public LocalDateTime loggedAt;
    }
}
