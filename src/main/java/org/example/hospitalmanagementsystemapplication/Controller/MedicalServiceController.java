package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.request.ServiceCreateRequest;
import com.hospital.management.model.dto.request.ServiceUpdateRequest;
import com.hospital.management.model.dto.response.MedicalServiceResponse;
import com.hospital.management.service.MedicalServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;


    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<MedicalServiceResponse> createService(
            @Valid @RequestBody ServiceCreateRequest request) {
        MedicalServiceResponse response = medicalServiceService.createService(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{serviceId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<MedicalServiceResponse> getServiceById(
            @PathVariable Long serviceId) {
        MedicalServiceResponse response = medicalServiceService.getServiceById(serviceId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<MedicalServiceResponse> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceUpdateRequest request) {
        MedicalServiceResponse response = medicalServiceService.updateService(serviceId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<MedicalServiceResponse> patchService(
            @PathVariable Long serviceId,
            @RequestBody ServiceUpdateRequest request) {
        MedicalServiceResponse response = medicalServiceService.patchService(serviceId, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{serviceId}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        medicalServiceService.deleteService(serviceId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<MedicalServiceResponse>> getServicesByHospital(
            @PathVariable Long hospitalId) {
        List<MedicalServiceResponse> services =
                medicalServiceService.getServicesByHospital(hospitalId);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Page<MedicalServiceResponse>> getAllServicesPaginated(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<MedicalServiceResponse> services =
                medicalServiceService.getAllServices(pageable);
        return ResponseEntity.ok(services);
    }


    @GetMapping("/with-doctor-count")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<MedicalServiceResponse>> getServicesWithDoctorCount(
            @RequestParam Long hospitalId) {
        List<MedicalServiceResponse> services =
                medicalServiceService.getServicesWithDoctorCount(hospitalId);
        return ResponseEntity.ok(services);
    }


    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PATIENT')")
    public ResponseEntity<List<MedicalServiceResponse>> searchServices(
            @RequestParam Long hospitalId,
            @RequestParam String searchTerm) {
        List<MedicalServiceResponse> services =
                medicalServiceService.searchServices(hospitalId, searchTerm);
        return ResponseEntity.ok(services);
    }


    @GetMapping("/most-requested")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<MedicalServiceResponse>> getMostRequestedServices(
            @RequestParam Long hospitalId) {
        List<MedicalServiceResponse> services =
                medicalServiceService.getMostRequestedServices(hospitalId);
        return ResponseEntity.ok(services);
    }
}