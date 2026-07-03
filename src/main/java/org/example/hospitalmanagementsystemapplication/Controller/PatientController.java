package org.example.hospitalmanagementsystemapplication.Controller;

import com.shecancode.hospitalms.dto.patient.PatientRegistrationRequest;
import com.shecancode.hospitalms.dto.patient.PatientResponse;
import com.shecancode.hospitalms.dto.patient.PatientUpdateRequest;
import com.shecancode.hospitalms.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<PatientResponse> register(@Valid @RequestBody PatientRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.register(request));
    }

    @GetMapping("/me")
    public ResponseEntity<PatientResponse> getMyProfile() {
        return ResponseEntity.ok(patientService.getMyProfile());
    }

    // Admin-only (enforced by SecurityConfig): view all registered patients
    @GetMapping
    public ResponseEntity<java.util.List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> update(@PathVariable Long id, @Valid @RequestBody PatientUpdateRequest request) {
        return ResponseEntity.ok(patientService.updateProfile(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PatientResponse> patch(@PathVariable Long id, @RequestBody PatientUpdateRequest request) {
        return ResponseEntity.ok(patientService.patchProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
