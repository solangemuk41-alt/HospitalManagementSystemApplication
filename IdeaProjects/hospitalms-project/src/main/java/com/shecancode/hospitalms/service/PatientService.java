package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.patient.PatientRegistrationRequest;
import com.shecancode.hospitalms.dto.patient.PatientResponse;
import com.shecancode.hospitalms.dto.patient.PatientUpdateRequest;
import com.shecancode.hospitalms.entity.Patient;
import com.shecancode.hospitalms.entity.enums.AppointmentStatus;
import com.shecancode.hospitalms.entity.enums.Role;
import com.shecancode.hospitalms.exception.BusinessRuleException;
import com.shecancode.hospitalms.exception.DuplicateResourceException;
import com.shecancode.hospitalms.exception.ResourceNotFoundException;
import com.shecancode.hospitalms.exception.UnauthorizedActionException;
import com.shecancode.hospitalms.mapper.PatientMapper;
import com.shecancode.hospitalms.repository.AppointmentRepository;
import com.shecancode.hospitalms.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientMapper patientMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PatientResponse register(PatientRegistrationRequest request) {
        if (patientRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("A patient with this email already exists");
        }
        if (request.getNationalId() != null && !request.getNationalId().isBlank()
                && patientRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("A patient with this national ID already exists");
        }

        Patient patient = Patient.builder()
                .nationalId(request.getNationalId())
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactPhone(request.getEmergencyContactPhone())
                .role(Role.PATIENT)
                .build();

        return patientMapper.toResponse(patientRepository.save(patient));
    }

    public PatientResponse getMyProfile() {
        return patientMapper.toResponse(currentUserProvider.currentPatient());
    }

    /** Administrator use case: view all registered patients. */
    public List<PatientResponse> getAllPatients() {
        currentUserProvider.currentAdministrator(); // throws if caller isn't an admin
        return patientRepository.findAll().stream().map(patientMapper::toResponse).toList();
    }

    public PatientResponse getById(Long id) {
        // Patients cannot view other patients' profiles; only admins call this via a different endpoint
        Patient current = currentUserProvider.currentPatient();
        if (!current.getId().equals(id)) {
            throw new UnauthorizedActionException("Patients cannot modify another patient's profile");
        }
        return patientMapper.toResponse(current);
    }

    @Transactional
    public PatientResponse updateProfile(Long id, PatientUpdateRequest request) {
        Patient patient = getOwnedPatient(id);
        applyUpdates(patient, request);
        return patientMapper.toResponse(patientRepository.save(patient));
    }

    @Transactional
    public PatientResponse patchProfile(Long id, PatientUpdateRequest request) {
        // Same handler - only non-null fields are applied either way
        return updateProfile(id, request);
    }

    @Transactional
    public void deleteAccount(Long id) {
        Patient patient = getOwnedPatient(id);

        long activeAppointments = appointmentRepository.countByPatientIdAndStatusIn(
                patient.getId(), List.of(AppointmentStatus.PENDING, AppointmentStatus.APPROVED));

        if (activeAppointments > 0) {
            throw new BusinessRuleException(
                    "Cannot delete account while you have pending or approved appointments");
        }
        patientRepository.delete(patient);
    }

    private void applyUpdates(Patient patient, PatientUpdateRequest request) {
        if (request.getFullName() != null) patient.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) patient.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getEmergencyContactName() != null) patient.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
    }

    private Patient getOwnedPatient(Long id) {
        Patient current = currentUserProvider.currentPatient();
        if (!current.getId().equals(id)) {
            throw new UnauthorizedActionException("Patients cannot modify another patient's profile");
        }
        return current;
    }
}
