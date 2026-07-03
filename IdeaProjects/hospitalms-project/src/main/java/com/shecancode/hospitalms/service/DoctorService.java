package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.doctor.DoctorRequest;
import com.shecancode.hospitalms.dto.doctor.DoctorResponse;
import com.shecancode.hospitalms.dto.doctor.DoctorUpdateRequest;
import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.Doctor;
import com.shecancode.hospitalms.entity.HospitalService;
import com.shecancode.hospitalms.entity.enums.AppointmentStatus;
import com.shecancode.hospitalms.entity.enums.DoctorAvailability;
import com.shecancode.hospitalms.entity.enums.Role;
import com.shecancode.hospitalms.exception.BusinessRuleException;
import com.shecancode.hospitalms.exception.DuplicateResourceException;
import com.shecancode.hospitalms.exception.ResourceNotFoundException;
import com.shecancode.hospitalms.mapper.DoctorMapper;
import com.shecancode.hospitalms.repository.AppointmentRepository;
import com.shecancode.hospitalms.repository.DoctorRepository;
import com.shecancode.hospitalms.repository.HospitalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalServiceRepository hospitalServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorMapper doctorMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        Administrator admin = currentUserProvider.currentAdministrator();

        if (doctorRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("A doctor with this email already exists");
        }
        if (doctorRepository.existsByMedicalLicenseNumber(request.getMedicalLicenseNumber())) {
            throw new DuplicateResourceException("A doctor with this medical license number already exists");
        }

        List<HospitalService> services = resolveServices(request.getServiceIds(), admin.getHospital().getId());

        Doctor doctor = Doctor.builder()
                .medicalLicenseNumber(request.getMedicalLicenseNumber())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .specialisation(request.getSpecialisation())
                .availabilityStatus(DoctorAvailability.ACTIVE)
                .role(Role.DOCTOR)
                .hospital(admin.getHospital())
                .services(services)
                .build();

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request) {
        Administrator admin = currentUserProvider.currentAdministrator();
        Doctor doctor = getOwnedDoctor(id, admin.getHospital().getId());

        if (request.getFullName() != null) doctor.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) doctor.setPhoneNumber(request.getPhoneNumber());
        if (request.getGender() != null) doctor.setGender(request.getGender());
        if (request.getSpecialisation() != null) doctor.setSpecialisation(request.getSpecialisation());
        if (request.getServiceIds() != null) {
            doctor.setServices(resolveServices(request.getServiceIds(), admin.getHospital().getId()));
        }

        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorResponse setAvailability(Long id, DoctorAvailability status) {
        Administrator admin = currentUserProvider.currentAdministrator();
        Doctor doctor = getOwnedDoctor(id, admin.getHospital().getId());
        doctor.setAvailabilityStatus(status);
        return doctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Administrator admin = currentUserProvider.currentAdministrator();
        Doctor doctor = getOwnedDoctor(id, admin.getHospital().getId());

        long futureAppointments = appointmentRepository.countByDoctorIdAndAppointmentDateGreaterThanEqualAndStatusIn(
                doctor.getId(), LocalDate.now(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.APPROVED));

        if (futureAppointments > 0) {
            throw new BusinessRuleException(
                    "Doctor has future appointments and cannot be deleted. Mark as INACTIVE instead.");
        }
        doctorRepository.delete(doctor);
    }

    public List<DoctorResponse> getAllForCurrentHospital() {
        Administrator admin = currentUserProvider.currentAdministrator();
        return doctorRepository.findByHospitalId(admin.getHospital().getId())
                .stream().map(doctorMapper::toResponse).toList();
    }

    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        return doctorMapper.toResponse(doctor);
    }

    private List<HospitalService> resolveServices(List<Long> serviceIds, Long hospitalId) {
        // Prevent duplicate service assignment
        Set<Long> uniqueIds = new LinkedHashSet<>(serviceIds);
        if (uniqueIds.size() != serviceIds.size()) {
            throw new BusinessRuleException("A doctor cannot be assigned to duplicate services");
        }

        List<HospitalService> services = hospitalServiceRepository.findAllById(uniqueIds);
        if (services.size() != uniqueIds.size()) {
            throw new ResourceNotFoundException("One or more services were not found");
        }
        boolean allBelongToHospital = services.stream()
                .allMatch(s -> s.getHospital().getId().equals(hospitalId));
        if (!allBelongToHospital) {
            throw new BusinessRuleException("All services must belong to your hospital");
        }
        return services;
    }

    private Doctor getOwnedDoctor(Long id, Long hospitalId) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
        if (!doctor.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Doctor not found in your hospital");
        }
        return doctor;
    }
}
