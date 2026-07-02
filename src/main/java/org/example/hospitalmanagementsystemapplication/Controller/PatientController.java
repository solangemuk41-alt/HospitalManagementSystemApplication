package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.request.PatientRegistrationRequest;
import com.hospital.management.model.dto.request.PatientUpdateRequest;
import com.hospital.management.model.dto.response.AppointmentResponse;
import com.hospital.management.model.dto.response.PatientResponse;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @PostMapping("/register")
    public ResponseEntity<PatientResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<PatientResponse> getPatientProfile(
            @PathVariable Long patientId,
            Authentication authentication) {
        verifyPatientAccess(patientId, authentication);
        PatientResponse response = patientService.getPatientProfile(patientId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<PatientResponse> updatePatientProfile(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequest request,
            Authentication authentication) {
        verifyPatientAccess(patientId, authentication);
        PatientResponse response = patientService.updatePatientProfile(patientId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<PatientResponse> patchPatientProfile(
            @PathVariable Long patientId,
            @RequestBody PatientUpdateRequest request,
            Authentication authentication) {
        verifyPatientAccess(patientId, authentication);
        PatientResponse response = patientService.patchPatientProfile(patientId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patientId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> deletePatientAccount(
            @PathVariable Long patientId,
            Authentication authentication) {
        verifyPatientAccess(patientId, authentication);
        patientService.deletePatientAccount(patientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{patientId}/appointments")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(
            @PathVariable Long patientId,
            Authentication authentication) {
        verifyPatientAccess(patientId, authentication);
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    private void verifyPatientAccess(Long patientId, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(
                a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
            Long currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(patientId)) {
                throw new SecurityException("You can only access your own profile");
            }
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}