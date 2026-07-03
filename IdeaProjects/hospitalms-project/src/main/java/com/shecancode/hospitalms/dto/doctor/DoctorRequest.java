package com.shecancode.hospitalms.dto.doctor;

import com.shecancode.hospitalms.entity.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Used by the administrator to create a doctor. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequest {

    @NotBlank(message = "Medical license number is required")
    private String medicalLicenseNumber;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Specialisation is required")
    private String specialisation;

    @NotEmpty(message = "A doctor must be assigned at least one hospital service")
    private List<Long> serviceIds;
}
