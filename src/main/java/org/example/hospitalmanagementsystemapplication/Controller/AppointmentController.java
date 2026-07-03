package org.example.hospitalmanagementsystemapplication.Controller;

import com.shecancode.hospitalms.dto.appointment.AppointmentReassignRequest;
import com.shecancode.hospitalms.dto.appointment.AppointmentRequest;
import com.shecancode.hospitalms.dto.appointment.AppointmentResponse;
import com.shecancode.hospitalms.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(request));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<AppointmentResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.approve(id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<AppointmentResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.reject(id));
    }

    @PatchMapping("/{id}/reassign")
    public ResponseEntity<AppointmentResponse> reassign(@PathVariable Long id, @Valid @RequestBody AppointmentReassignRequest request) {
        return ResponseEntity.ok(appointmentService.reassign(id, request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.markCompleted(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancel(id));
    }

    @GetMapping("/my/patient")
    public ResponseEntity<List<AppointmentResponse>> myAsPatient() {
        return ResponseEntity.ok(appointmentService.getMyAppointmentsAsPatient());
    }

    @GetMapping("/my/doctor")
    public ResponseEntity<List<AppointmentResponse>> myAsDoctor() {
        return ResponseEntity.ok(appointmentService.getMyAppointmentsAsDoctor());
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> all() {
        return ResponseEntity.ok(appointmentService.getAllForCurrentHospital());
    }
}
