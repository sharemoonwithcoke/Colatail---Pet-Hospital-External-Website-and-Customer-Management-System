package com.example.backend.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WellnessLogRepository extends JpaRepository<WellnessLog, UUID> {
    List<WellnessLog> findByPet_IdOrderByLoggedAtDesc(UUID petId);
}
