package org.example.hospitalmanagementsystemapplication.Dtos;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used for both PUT (full update) and PATCH (partial update).
 * For PATCH, only non-null fields are applied.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequest {
    private String fullName;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Phone number format is invalid")
    private String phoneNumber;

    private String address;
    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Emergency contact phone format is invalid")
    private String emergencyContactPhone;
}
