package com.example.backend.Controller;

import com.example.backend.Customer.CaseRecord;
import com.example.backend.Customer.CaseRecordRepository;
import com.example.backend.Customer.Pet;
import com.example.backend.Customer.PersonRepository;
import com.example.backend.Customer.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetRepository petRepository;
    private final PersonRepository personRepository;
    private final CaseRecordRepository caseRecordRepository;

    @Autowired
    public PetController(PetRepository petRepository, PersonRepository personRepository,
                         CaseRecordRepository caseRecordRepository) {
        this.petRepository = petRepository;
        this.personRepository = personRepository;
        this.caseRecordRepository = caseRecordRepository;
    }

    @GetMapping
    public List<Pet> getAll() {
        return petRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getById(@PathVariable Long id) {
        return petRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public List<Pet> getByOwner(@PathVariable Long ownerId) {
        return petRepository.findByOwner_Id(ownerId);
    }

    @PostMapping
    public ResponseEntity<Pet> create(@RequestBody PetRequest req) {
        return personRepository.findById(req.ownerId).map(owner -> {
            Pet pet = new Pet(req.name, req.type, req.species, req.color, req.gender, req.birthday, owner);
            return ResponseEntity.ok(petRepository.save(pet));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> update(@PathVariable Long id, @RequestBody PetRequest req) {
        return petRepository.findById(id).map(pet -> {
            pet.setName(req.name);
            pet.setType(req.type);
            pet.setSpecies(req.species);
            pet.setColor(req.color);
            pet.setGender(req.gender);
            pet.setBirthday(req.birthday);
            return ResponseEntity.ok(petRepository.save(pet));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!petRepository.existsById(id)) return ResponseEntity.notFound().build();
        petRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/case-records")
    public List<CaseRecord> getCaseRecords(@PathVariable Long id) {
        return caseRecordRepository.findByPet_IdOrderByDateDesc(id);
    }

    static class PetRequest {
        public String name, type, species, color, gender;
        public LocalDate birthday;
        public Long ownerId;
    }
}
