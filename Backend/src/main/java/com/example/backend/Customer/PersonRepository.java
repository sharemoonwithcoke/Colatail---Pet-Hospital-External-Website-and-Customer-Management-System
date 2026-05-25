package com.example.backend.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

    @Query("SELECT p FROM Person p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.email)     LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "p.phoneNumber LIKE CONCAT('%', :q, '%')")
    List<Person> searchByQuery(@Param("q") String query);
}
