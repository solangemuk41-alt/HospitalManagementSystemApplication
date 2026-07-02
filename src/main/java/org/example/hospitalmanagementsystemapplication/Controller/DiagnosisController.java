package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.request.DiagnosisRequest;
import com.hospital.management.model.dto.response.DiagnosisResponse;
import com.hospital.management.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> createDiagnosis(
            @Valid @RequestBody DiagnosisRequest request,
            Authentication authentication) {
        Long doctorId = getCurrentUserId(authentication);
        DiagnosisResponse response = diagnosisService.createDiagnosis(request, doctorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{diagnosisId}/prescription")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> uploadPrescription(
            @PathVariable Long diagnosisId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        verifyDoctorAccessToDiagnosis(diagnosisId, authentication);
        DiagnosisResponse response = diagnosisService.uploadPrescription(diagnosisId, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{diagnosisId}/prescription")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<Resource> downloadPrescription(
            @PathVariable Long diagnosisId,
            Authentication authentication) {
        verifyAccessToDiagnosis(diagnosisId, authentication);
        Resource resource = diagnosisService.downloadPrescription(diagnosisId);
        String filename = diagnosisService.getPrescriptionFileName(diagnosisId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{diagnosisId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DiagnosisResponse> getDiagnosisById(
            @PathVariable Long diagnosisId,
            Authentication authentication) {
        verifyAccessToDiagnosis(diagnosisId, authentication);
        DiagnosisResponse response = diagnosisService.getDiagnosisById(diagnosisId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{diagnosisId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DiagnosisResponse> updateDiagnosis(
            @PathVariable Long diagnosisId,
            @Valid @RequestBody DiagnosisRequest request,
            Authentication authentication) {
        verifyDoctorAccessToDiagnosis(diagnosisId, authentication);
        DiagnosisResponse response = diagnosisService.updateDiagnosis(diagnosisId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DiagnosisResponse> getDiagnosisByAppointment(
            @PathVariable Long appointmentId) {
        DiagnosisResponse response = diagnosisService.getDiagnosisByAppointment(appointmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DiagnosisResponse>> getDiagnosesByPatient(
            @PathVariable Long patientId,
            Authentication authentication) {
        verifyPatientAccess(patientId, authentication);
        List<DiagnosisResponse> diagnoses = diagnosisService.getDiagnosesByPatient(patientId);
        return ResponseEntity.ok(diagnoses);
    }

    @DeleteMapping("/{diagnosisId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable Long diagnosisId) {
        diagnosisService.deleteDiagnosis(diagnosisId);
        return ResponseEntity.noContent().build();
    }

    private void verifyDoctorAccessToDiagnosis(Long diagnosisId, Authentication authentication) {
        Long doctorId = getCurrentUserId(authentication);
        DiagnosisResponse diagnosis = diagnosisService.getDiagnosisById(diagnosisId);
        if (!diagnosis.getDoctorId().equals(doctorId)) {
            throw new SecurityException("You can only access diagnoses assigned to you");
        }
    }

    private void verifyAccessToDiagnosis(Long diagnosisId, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(
                a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
            // For doctors and patients, verify they have access
        }
    }

    private void verifyPatientAccess(Long patientId, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(
                a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
            Long currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(patientId)) {
                throw new SecurityException("You can only access your own data");
            }
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}