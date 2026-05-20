package com.example.backend.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CaseRecordRepository extends JpaRepository<CaseRecord, Long> {
    List<CaseRecord> findByPet_IdOrderByDateDesc(Long petId);
}
