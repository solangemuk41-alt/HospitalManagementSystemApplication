package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<Administrator> findByEmailIgnoreCase(String email);
    Optional<Administrator> findByHospitalId(Long hospitalId);
}
