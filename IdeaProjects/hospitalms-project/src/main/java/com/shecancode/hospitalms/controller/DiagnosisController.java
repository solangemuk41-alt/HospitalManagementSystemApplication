package com.shecancode.hospitalms.controller;

import com.shecancode.hospitalms.dto.diagnosis.DiagnosisRequest;
import com.shecancode.hospitalms.dto.diagnosis.DiagnosisResponse;
import com.shecancode.hospitalms.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    // multipart/form-data: "request" part (JSON) + optional "prescriptionFile" part (PDF)
    @PostMapping(value = "/appointments/{appointmentId}", consumes = "multipart/form-data")
    public ResponseEntity<DiagnosisResponse> createOrUpdate(
            @PathVariable Long appointmentId,
            @Valid @RequestPart("request") DiagnosisRequest request,
            @RequestPart(value = "prescriptionFile", required = false) MultipartFile prescriptionFile) {
        return ResponseEntity.ok(diagnosisService.createOrUpdate(appointmentId, request, prescriptionFile));
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<DiagnosisResponse> getByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(diagnosisService.getByAppointment(appointmentId));
    }
}
