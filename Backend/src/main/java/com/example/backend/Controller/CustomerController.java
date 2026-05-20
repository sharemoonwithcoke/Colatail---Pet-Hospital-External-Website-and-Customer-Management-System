package com.example.backend.Controller;

import com.example.backend.Customer.Person;
import com.example.backend.Customer.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final PersonRepository personRepository;

    @Autowired
    public CustomerController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Person> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return personRepository.searchByQuery(search);
        }
        return personRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable Long id) {
        return personRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody Person person) {
        return ResponseEntity.ok(personRepository.save(person));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable Long id, @RequestBody Person updated) {
        return personRepository.findById(id).map(p -> {
            p.setFirstName(updated.getFirstName());
            p.setLastName(updated.getLastName());
            p.setPhoneNumber(updated.getPhoneNumber());
            p.setEmail(updated.getEmail());
            p.setAddress(updated.getAddress());
            return ResponseEntity.ok(personRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!personRepository.existsById(id)) return ResponseEntity.notFound().build();
        personRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
