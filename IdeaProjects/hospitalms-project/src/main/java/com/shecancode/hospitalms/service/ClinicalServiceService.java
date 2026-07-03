package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.service.ServiceRequest;
import com.shecancode.hospitalms.dto.service.ServiceResponse;
import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.HospitalService;
import com.shecancode.hospitalms.exception.DuplicateResourceException;
import com.shecancode.hospitalms.exception.ResourceNotFoundException;
import com.shecancode.hospitalms.mapper.ServiceMapper;
import com.shecancode.hospitalms.repository.HospitalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages HospitalService entities (Module 4: Maternity, Pediatrics, etc).
 * Named "Clinical" to avoid clashing with the HospitalService entity's simple name.
 */
@Service
@RequiredArgsConstructor
public class ClinicalServiceService {

    private final HospitalServiceRepository hospitalServiceRepository;
    private final ServiceMapper serviceMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        Administrator admin = currentUserProvider.currentAdministrator();
        Long hospitalId = admin.getHospital().getId();

        if (hospitalServiceRepository.existsByNameIgnoreCaseAndHospitalId(request.getName(), hospitalId)) {
            throw new DuplicateResourceException("A service with this name already exists in your hospital");
        }

        HospitalService service = HospitalService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .hospital(admin.getHospital())
                .build();
        return serviceMapper.toResponse(hospitalServiceRepository.save(service));
    }

    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        Administrator admin = currentUserProvider.currentAdministrator();
        HospitalService service = getOwnedService(id, admin.getHospital().getId());

        if (!service.getName().equalsIgnoreCase(request.getName())
                && hospitalServiceRepository.existsByNameIgnoreCaseAndHospitalId(request.getName(), admin.getHospital().getId())) {
            throw new DuplicateResourceException("A service with this name already exists in your hospital");
        }

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        return serviceMapper.toResponse(hospitalServiceRepository.save(service));
    }

    @Transactional
    public void delete(Long id) {
        Administrator admin = currentUserProvider.currentAdministrator();
        HospitalService service = getOwnedService(id, admin.getHospital().getId());
        // Deleting a service must NOT affect doctor records - the ManyToMany
        // join rows are removed but Doctor entities remain untouched.
        hospitalServiceRepository.delete(service);
    }

    public List<ServiceResponse> getAllForCurrentHospital() {
        Administrator admin = currentUserProvider.currentAdministrator();
        return hospitalServiceRepository.findByHospitalId(admin.getHospital().getId())
                .stream().map(serviceMapper::toResponse).toList();
    }

    public ServiceResponse getById(Long id) {
        HospitalService service = hospitalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return serviceMapper.toResponse(service);
    }

    private HospitalService getOwnedService(Long id, Long hospitalId) {
        HospitalService service = hospitalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        if (!service.getHospital().getId().equals(hospitalId)) {
            throw new ResourceNotFoundException("Service not found in your hospital");
        }
        return service;
    }
}
