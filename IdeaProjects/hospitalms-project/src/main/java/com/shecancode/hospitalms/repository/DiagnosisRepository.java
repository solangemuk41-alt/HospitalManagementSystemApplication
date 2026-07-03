package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByAppointmentId(Long appointmentId);
}
