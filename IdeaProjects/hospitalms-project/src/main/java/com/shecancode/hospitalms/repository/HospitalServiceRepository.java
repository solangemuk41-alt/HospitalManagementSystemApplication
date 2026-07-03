package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.HospitalService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HospitalServiceRepository extends JpaRepository<HospitalService, Long> {
    boolean existsByNameIgnoreCaseAndHospitalId(String name, Long hospitalId);
    Optional<HospitalService> findByNameIgnoreCaseAndHospitalId(String name, Long hospitalId);
    List<HospitalService> findByHospitalId(Long hospitalId);
}
