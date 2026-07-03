package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.diagnosis.DiagnosisRequest;
import com.shecancode.hospitalms.dto.diagnosis.DiagnosisResponse;
import com.shecancode.hospitalms.entity.Appointment;
import com.shecancode.hospitalms.entity.Diagnosis;
import com.shecancode.hospitalms.entity.Doctor;
import com.shecancode.hospitalms.entity.enums.AppointmentStatus;
import com.shecancode.hospitalms.exception.BusinessRuleException;
import com.shecancode.hospitalms.exception.ResourceNotFoundException;
import com.shecancode.hospitalms.exception.UnauthorizedActionException;
import com.shecancode.hospitalms.mapper.DiagnosisMapper;
import com.shecancode.hospitalms.repository.AppointmentRepository;
import com.shecancode.hospitalms.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    private final DiagnosisRepository diagnosisRepository;
    private final AppointmentRepository appointmentRepository;
    private final DiagnosisMapper diagnosisMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DiagnosisResponse createOrUpdate(Long appointmentId, DiagnosisRequest request, MultipartFile prescriptionFile) {
        Doctor doctor = currentUserProvider.currentDoctor();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Only the assigned doctor can diagnose
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new UnauthorizedActionException("You can only diagnose patients assigned to you");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Diagnosis is read-only once the appointment is completed");
        }

        Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> Diagnosis.builder().appointment(appointment).doctor(doctor).build());

        diagnosis.setSymptoms(request.getSymptoms());
        diagnosis.setDiagnosisNotes(request.getDiagnosisNotes());

        if (prescriptionFile != null && !prescriptionFile.isEmpty()) {
            diagnosis.setPrescriptionFilePath(storePrescriptionFile(prescriptionFile));
        }

        return diagnosisMapper.toResponse(diagnosisRepository.save(diagnosis));
    }

    public DiagnosisResponse getByAppointment(Long appointmentId) {
        Diagnosis diagnosis = diagnosisRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found for this appointment"));
        return diagnosisMapper.toResponse(diagnosis);
    }

    private String storePrescriptionFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new BusinessRuleException("Prescription file must be a PDF");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessRuleException("Prescription file must not exceed 5MB");
        }

        try {
            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + ".pdf";
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to store prescription file: " + e.getMessage());
        }
    }
}
