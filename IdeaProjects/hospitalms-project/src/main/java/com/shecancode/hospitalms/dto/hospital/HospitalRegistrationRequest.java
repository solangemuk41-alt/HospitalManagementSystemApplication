package com.shecancode.hospitalms.dto.hospital;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registers a hospital AND its administrator in one call
 * (the admin is auto-created when the hospital is registered).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRegistrationRequest {

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @NotBlank(message = "Hospital telephone is required")
    private String telephone;

    @NotBlank(message = "Hospital address is required")
    private String address;

    @NotBlank(message = "Administrator full name is required")
    private String adminFullName;

    @NotBlank(message = "Administrator email is required")
    @Email(message = "Administrator email must be valid")
    private String adminEmail;

    @NotBlank(message = "Administrator password is required")
    private String adminPassword;

    @NotBlank(message = "Administrator phone number is required")
    private String adminPhoneNumber;
}
