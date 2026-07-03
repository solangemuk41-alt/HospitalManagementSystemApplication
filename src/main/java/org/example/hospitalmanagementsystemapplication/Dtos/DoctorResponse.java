package org.example.hospitalmanagementsystemapplication.Dtos;

import com.shecancode.hospitalms.entity.enums.DoctorAvailability;
import com.shecancode.hospitalms.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long id;
    private String medicalLicenseNumber;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private String specialisation;
    private DoctorAvailability availabilityStatus;
    private Long hospitalId;
    private List<String> serviceNames;
}
