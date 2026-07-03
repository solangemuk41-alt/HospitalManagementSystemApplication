package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.hospital.HospitalRegistrationRequest;
import com.shecancode.hospitalms.dto.hospital.HospitalResponse;
import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.Hospital;
import com.shecancode.hospitalms.entity.enums.Role;
import com.shecancode.hospitalms.exception.DuplicateResourceException;
import com.shecancode.hospitalms.exception.ResourceNotFoundException;
import com.shecancode.hospitalms.mapper.HospitalMapper;
import com.shecancode.hospitalms.repository.AdministratorRepository;
import com.shecancode.hospitalms.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HospitalRegistrationService {

    private final HospitalRepository hospitalRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final HospitalMapper hospitalMapper;

    @Transactional
    public HospitalResponse registerHospital(HospitalRegistrationRequest request) {
        if (hospitalRepository.existsByNameIgnoreCase(request.getHospitalName())) {
            throw new DuplicateResourceException("A hospital with this name already exists");
        }
        if (administratorRepository.existsByEmailIgnoreCase(request.getAdminEmail())) {
            throw new DuplicateResourceException("An administrator with this email already exists");
        }

        Hospital hospital = Hospital.builder()
                .name(request.getHospitalName())
                .telephone(request.getTelephone())
                .address(request.getAddress())
                .build();
        hospital = hospitalRepository.save(hospital);

        Administrator administrator = Administrator.builder()
                .fullName(request.getAdminFullName())
                .email(request.getAdminEmail())
                .password(passwordEncoder.encode(request.getAdminPassword()))
                .phoneNumber(request.getAdminPhoneNumber())
                .role(Role.ADMIN)
                .hospital(hospital)
                .build();
        administrator = administratorRepository.save(administrator);

        hospital.setAdministrator(administrator);
        return hospitalMapper.toResponse(hospital);
    }

    public HospitalResponse getHospital(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));
        return hospitalMapper.toResponse(hospital);
    }
}
