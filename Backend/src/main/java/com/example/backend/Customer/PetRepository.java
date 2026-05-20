package com.example.backend.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByOwner_Id(Long ownerId);
}
