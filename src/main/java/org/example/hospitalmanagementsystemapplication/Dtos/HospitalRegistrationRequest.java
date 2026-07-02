package org.example.hospitalmanagementsystemapplication.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class HospitalRegistrationRequest {
    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "Telephone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid telephone format")
    private String telephone;

    @NotBlank(message = "Physical address is required")
    private String physicalAddress;

    @NotBlank(message = "Administrator full name is required")
    private String adminFullName;

    @NotBlank(message = "Administrator email is required")
    @Email(message = "Invalid email format")
    private String adminEmail;

    @NotBlank(message = "Administrator password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters and contain uppercase, lowercase, number and special character")
    private String adminPassword;

    @NotBlank(message = "Administrator phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone format")
    private String adminPhone;
}
