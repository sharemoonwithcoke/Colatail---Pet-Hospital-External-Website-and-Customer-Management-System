package com.example.backend.Controller;

import com.example.backend.Customer.Person;
import com.example.backend.Customer.PersonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final PersonRepository personRepository;

    public CustomerController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Person> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) return personRepository.searchByQuery(search);
        return personRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable UUID id) {
        return personRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person) {
        return ResponseEntity.ok(personRepository.save(person));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable UUID id, @RequestBody Person updated) {
        return personRepository.findById(id).map(p -> {
            p.setName(updated.getName());
            p.setPhone(updated.getPhone());
            p.setEmail(updated.getEmail());
            p.setAddress(updated.getAddress());
            return ResponseEntity.ok(personRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!personRepository.existsById(id)) return ResponseEntity.notFound().build();
        personRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
