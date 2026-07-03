package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.request.DoctorCreateRequest;
import com.hospital.management.model.dto.request.DoctorUpdateRequest;
import com.hospital.management.model.dto.response.AppointmentResponse;
import com.hospital.management.model.dto.response.DoctorResponse;
import com.hospital.management.model.dto.response.DiagnosisResponse;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.DiagnosisService;
import com.hospital.management.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DiagnosisService diagnosisService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<DoctorResponse> createDoctor(
            @Valid @RequestBody DoctorCreateRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long doctorId) {
        DoctorResponse response = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorUpdateRequest request) {
        DoctorResponse response = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<DoctorResponse> patchDoctor(
            @PathVariable Long doctorId,
            @RequestBody DoctorUpdateRequest request) {
        DoctorResponse response = doctorService.patchDoctor(doctorId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{doctorId}/status")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<DoctorResponse> updateDoctorStatus(
            @PathVariable Long doctorId,
            @RequestParam boolean active) {
        DoctorResponse response = doctorService.updateDoctorStatus(doctorId, active);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByHospital(
            @PathVariable Long hospitalId) {
        List<DoctorResponse> doctors = doctorService.getDoctorsByHospital(hospitalId);
        return ResponseEntity.ok(doctors);
    }


    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByService(
            @PathVariable Long serviceId) {
        List<DoctorResponse> doctors = doctorService.getDoctorsByService(serviceId);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/available/service/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<List<DoctorResponse>> getAvailableDoctorsByService(
            @PathVariable Long serviceId) {
        List<DoctorResponse> doctors = doctorService.getAvailableDoctorsByService(serviceId);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{doctorId}/appointments")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @PathVariable Long doctorId,
            Authentication authentication) {
        verifyDoctorAccess(doctorId, authentication);
        List<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{doctorId}/appointments/paginated")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public ResponseEntity<Page<AppointmentResponse>> getDoctorAppointmentsPaginated(
            @PathVariable Long doctorId,
            @PageableDefault(size = 10, sort = "appointmentDate") Pageable pageable,
            Authentication authentication) {
        verifyDoctorAccess(doctorId, authentication);
        Page<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByDoctor(doctorId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{doctorId}/diagnoses")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public ResponseEntity<List<DiagnosisResponse>> getDoctorDiagnoses(
            @PathVariable Long doctorId,
            Authentication authentication) {
        verifyDoctorAccess(doctorId, authentication);
        List<DiagnosisResponse> diagnoses =
                diagnosisService.getDiagnosesByDoctor(doctorId);
        return ResponseEntity.ok(diagnoses);
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Page<DoctorResponse>> getAllDoctorsPaginated(
            @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        Page<DoctorResponse> doctors = doctorService.getAllDoctors(pageable);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<DoctorResponse>> searchDoctors(
            @RequestParam String searchTerm,
            @RequestParam(required = false) Long hospitalId) {
        List<DoctorResponse> doctors = doctorService.searchDoctors(searchTerm, hospitalId);
        return ResponseEntity.ok(doctors);
    }


    private void verifyDoctorAccess(Long doctorId, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(
                a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {
            Long currentUserId = getCurrentUserId(authentication);
            if (!currentUserId.equals(doctorId)) {
                throw new SecurityException("You can only access your own data");
            }
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}