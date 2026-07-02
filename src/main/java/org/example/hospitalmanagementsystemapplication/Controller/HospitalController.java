package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.request.HospitalRegistrationRequest;
import com.hospital.management.model.dto.response.HospitalResponse;
import com.hospital.management.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping("/register")
    public ResponseEntity<HospitalResponse> registerHospital(
            @Valid @RequestBody HospitalRegistrationRequest request) {
        HospitalResponse response = hospitalService.registerHospital(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{hospitalId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<HospitalResponse> getHospitalDetails(@PathVariable Long hospitalId) {
        HospitalResponse response = hospitalService.getHospitalDetails(hospitalId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{hospitalId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<HospitalResponse> updateHospital(
            @PathVariable Long hospitalId,
            @Valid @RequestBody HospitalRegistrationRequest request) {
        HospitalResponse response = hospitalService.updateHospital(hospitalId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{hospitalId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<HospitalResponse> patchHospital(
            @PathVariable Long hospitalId,
            @RequestBody HospitalRegistrationRequest request) {
        HospitalResponse response = hospitalService.patchHospital(hospitalId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{hospitalId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long hospitalId) {
        hospitalService.deleteHospital(hospitalId);
        return ResponseEntity.noContent().build();
    }
}