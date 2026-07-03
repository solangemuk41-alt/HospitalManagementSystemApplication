package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.Doctor;
import com.shecancode.hospitalms.entity.Patient;
import com.shecancode.hospitalms.exception.UnauthorizedActionException;
import com.shecancode.hospitalms.repository.AdministratorRepository;
import com.shecancode.hospitalms.repository.DoctorRepository;
import com.shecancode.hospitalms.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated user (by email from the JWT/SecurityContext)
 * into the concrete Administrator / Doctor / Patient entity.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final AdministratorRepository administratorRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public String currentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Administrator currentAdministrator() {
        return administratorRepository.findByEmailIgnoreCase(currentEmail())
                .orElseThrow(() -> new UnauthorizedActionException("Current user is not an administrator"));
    }

    public Doctor currentDoctor() {
        return doctorRepository.findByEmailIgnoreCase(currentEmail())
                .orElseThrow(() -> new UnauthorizedActionException("Current user is not a doctor"));
    }

    public Patient currentPatient() {
        return patientRepository.findByEmailIgnoreCase(currentEmail())
                .orElseThrow(() -> new UnauthorizedActionException("Current user is not a patient"));
    }
}
