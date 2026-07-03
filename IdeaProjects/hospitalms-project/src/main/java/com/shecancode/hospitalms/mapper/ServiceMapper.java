package com.shecancode.hospitalms.mapper;

import com.shecancode.hospitalms.dto.service.ServiceResponse;
import com.shecancode.hospitalms.entity.HospitalService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "hospitalId", source = "hospital.id")
    ServiceResponse toResponse(HospitalService hospitalService);
}
