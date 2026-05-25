package com.example.backend.Controller;

import com.example.backend.Customer.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetRepository petRepository;
    private final PersonRepository personRepository;
    private final CaseRecordRepository caseRecordRepository;
    private final WellnessLogRepository wellnessLogRepository;

    public PetController(PetRepository petRepository, PersonRepository personRepository,
                         CaseRecordRepository caseRecordRepository, WellnessLogRepository wellnessLogRepository) {
        this.petRepository = petRepository;
        this.personRepository = personRepository;
        this.caseRecordRepository = caseRecordRepository;
        this.wellnessLogRepository = wellnessLogRepository;
    }

    @GetMapping
    public List<Pet> getAll() { return petRepository.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getById(@PathVariable UUID id) {
        return petRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public List<Pet> getByOwner(@PathVariable UUID ownerId) {
        return petRepository.findByOwner_Id(ownerId);
    }

    @PostMapping
    public ResponseEntity<Pet> create(@RequestBody PetRequest req) {
        return personRepository.findById(req.ownerId).map(owner -> {
            Pet pet = new Pet();
            pet.setName(req.name);
            pet.setBreed(req.breed);
            if (req.gender != null) pet.setGender(Pet.Gender.valueOf(req.gender.toUpperCase()));
            pet.setBirthday(req.birthday);
            pet.setPhotoUrl(req.photoUrl);
            pet.setOwner(owner);
            return ResponseEntity.ok(petRepository.save(pet));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> update(@PathVariable UUID id, @RequestBody PetRequest req) {
        return petRepository.findById(id).map(pet -> {
            pet.setName(req.name);
            pet.setBreed(req.breed);
            if (req.gender != null) pet.setGender(Pet.Gender.valueOf(req.gender.toUpperCase()));
            pet.setBirthday(req.birthday);
            pet.setPhotoUrl(req.photoUrl);
            return ResponseEntity.ok(petRepository.save(pet));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!petRepository.existsById(id)) return ResponseEntity.notFound().build();
        petRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/case-records")
    public List<CaseRecord> getCaseRecords(@PathVariable UUID id) {
        return caseRecordRepository.findByPet_IdOrderByVisitedAtDesc(id);
    }

    @GetMapping("/{id}/wellness-logs")
    public List<WellnessLog> getWellnessLogs(@PathVariable UUID id) {
        return wellnessLogRepository.findByPet_IdOrderByLoggedAtDesc(id);
    }

    static class PetRequest {
        public String name, breed, gender, photoUrl;
        public LocalDate birthday;
        public UUID ownerId;
    }
}
