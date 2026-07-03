package com.shecancode.hospitalms.mapper;

import com.shecancode.hospitalms.dto.doctor.DoctorResponse;
import com.shecancode.hospitalms.entity.Doctor;
import com.shecancode.hospitalms.entity.HospitalService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "serviceNames", source = "services", qualifiedByName = "servicesToNames")
    DoctorResponse toResponse(Doctor doctor);

    @org.mapstruct.Named("servicesToNames")
    default List<String> servicesToNames(List<HospitalService> services) {
        if (services == null) return List.of();
        return services.stream().map(HospitalService::getName).toList();
    }
}
