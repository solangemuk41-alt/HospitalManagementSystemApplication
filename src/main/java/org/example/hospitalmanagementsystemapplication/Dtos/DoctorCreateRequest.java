package org.example.hospitalmanagementsystemapplication.Dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class DoctorCreateRequest {
    @NotBlank(message = "Medical license number is required")
    private String medicalLicenseNumber;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone format")
    private String phoneNumber;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Specialisation is required")
    private String specialisation;

    @NotNull(message = "At least one service is required")
    @Size(min = 1, message = "At least one service is required")
    private List<Long> serviceIds;
}