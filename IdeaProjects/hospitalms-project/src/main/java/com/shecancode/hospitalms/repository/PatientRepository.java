package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByNationalId(String nationalId);
    Optional<Patient> findByEmailIgnoreCase(String email);
}
