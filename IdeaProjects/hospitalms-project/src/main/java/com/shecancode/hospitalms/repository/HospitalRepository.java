package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Hospital> findByNameIgnoreCase(String name);
}
