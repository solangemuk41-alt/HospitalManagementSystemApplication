package com.shecancode.hospitalms.mapper;

import com.shecancode.hospitalms.dto.diagnosis.DiagnosisResponse;
import com.shecancode.hospitalms.entity.Diagnosis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DiagnosisMapper {

    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", source = "doctor.fullName")
    @Mapping(target = "readOnly", expression = "java(diagnosis.isReadOnly())")
    DiagnosisResponse toResponse(Diagnosis diagnosis);
}
