package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.request.AppointmentRequest;
import com.hospital.management.model.dto.request.AppointmentUpdateRequest;
import com.hospital.management.model.dto.response.AppointmentResponse;
import com.hospital.management.service.AppointmentService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        Long patientId = getCurrentUserId(authentication);
        AppointmentResponse response = appointmentService.bookAppointment(request, patientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        verifyAppointmentAccess(appointmentId, authentication);
        AppointmentResponse response = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentUpdateRequest request,
            Authentication authentication) {
        verifyAppointmentAccess(appointmentId, authentication);
        AppointmentResponse response = appointmentService.updateAppointment(appointmentId, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        verifyAppointmentAccess(appointmentId, authentication);
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{appointmentId}/approve")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AppointmentResponse> approveAppointment(
            @PathVariable Long appointmentId) {
        AppointmentResponse response = appointmentService.approveAppointment(appointmentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{appointmentId}/reject")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AppointmentResponse> rejectAppointment(
            @PathVariable Long appointmentId,
            @RequestParam(required = false) String reason) {
        AppointmentResponse response = appointmentService.rejectAppointment(appointmentId, reason);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication) {
        verifyDoctorAccessToAppointment(appointmentId, authentication);
        AppointmentResponse response = appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long hospitalId) {
        List<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByDateRange(startDate, endDate, hospitalId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<AppointmentResponse>> getPendingAppointments(
            @RequestParam Long hospitalId) {
        List<AppointmentResponse> appointments =
                appointmentService.getPendingAppointments(hospitalId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByStatus(
            @PathVariable String status,
            @RequestParam Long hospitalId) {
        List<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByStatus(status, hospitalId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getTodayAppointments(
            @RequestParam(required = false) Long hospitalId,
            Authentication authentication) {
        Long doctorId = null;
        if (authentication.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            doctorId = getCurrentUserId(authentication);
        }
        List<AppointmentResponse> appointments =
                appointmentService.getTodayAppointments(hospitalId, doctorId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Page<AppointmentResponse>> getAllAppointmentsPaginated(
            @RequestParam Long hospitalId,
            @PageableDefault(size = 20, sort = "appointmentDate") Pageable pageable) {
        Page<AppointmentResponse> appointments =
                appointmentService.getAllAppointments(hospitalId, pageable);
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{appointmentId}/reassign")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<AppointmentResponse> reassignAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long newDoctorId) {
        AppointmentResponse response =
                appointmentService.reassignAppointment(appointmentId, newDoctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-availability")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<Boolean> checkDoctorAvailability(
            @RequestParam Long doctorId,
            @RequestParam LocalDate date) {
        boolean available = appointmentService.isDoctorAvailable(doctorId, date);
        return ResponseEntity.ok(available);
    }


    @GetMapping("/available-slots")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam LocalDate date) {
        List<String> slots = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(slots);
    }


    private void verifyAppointmentAccess(Long appointmentId, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(
                a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"))) {

        }
    }

    private void verifyDoctorAccessToAppointment(Long appointmentId, Authentication authentication) {
        // Check if the authenticated doctor is assigned to this appointment
        Long doctorId = getCurrentUserId(authentication);
        AppointmentResponse appointment = appointmentService.getAppointmentById(appointmentId);
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new SecurityException("You can only access appointments assigned to you");
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}