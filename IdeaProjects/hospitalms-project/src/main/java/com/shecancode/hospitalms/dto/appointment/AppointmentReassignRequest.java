package com.shecancode.hospitalms.dto.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReassignRequest {

    @NotNull(message = "New doctor id is required")
    private Long newDoctorId;
}
