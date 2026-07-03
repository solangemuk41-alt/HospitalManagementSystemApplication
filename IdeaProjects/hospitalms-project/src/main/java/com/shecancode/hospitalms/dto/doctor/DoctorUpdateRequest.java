package com.shecancode.hospitalms.dto.doctor;

import com.shecancode.hospitalms.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Fields an admin may update on an existing doctor. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private Gender gender;
    private String specialisation;
    private List<Long> serviceIds;
}
