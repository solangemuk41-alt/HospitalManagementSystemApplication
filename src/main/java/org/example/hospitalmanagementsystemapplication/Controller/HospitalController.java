package org.example.hospitalmanagementsystemapplication.Controller;

import com.shecancode.hospitalms.dto.hospital.HospitalRegistrationRequest;
import com.shecancode.hospitalms.dto.hospital.HospitalResponse;
import com.shecancode.hospitalms.service.HospitalRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalRegistrationService hospitalRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<HospitalResponse> register(@Valid @RequestBody HospitalRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalRegistrationService.registerHospital(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponse> getHospital(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalRegistrationService.getHospital(id));
    }
}
