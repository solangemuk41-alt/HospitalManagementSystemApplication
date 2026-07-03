package com.shecancode.hospitalms.mapper;

import com.shecancode.hospitalms.dto.patient.PatientResponse;
import com.shecancode.hospitalms.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientResponse toResponse(Patient patient);
}
