package com.shecancode.hospitalms.mapper;

import com.shecancode.hospitalms.dto.hospital.HospitalResponse;
import com.shecancode.hospitalms.entity.Hospital;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HospitalMapper {

    @Mapping(target = "adminFullName", source = "administrator.fullName")
    @Mapping(target = "adminEmail", source = "administrator.email")
    HospitalResponse toResponse(Hospital hospital);
}
