package org.example.hospitalmanagementsystemapplication.Dtos;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    @NotNull(message = "Appointment date is required")
    @FutureOrPresent(message = "Appointment date cannot be in the past")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    private String symptoms;

    @NotBlank(message = "Reason for visit is required")
    private String reasonForVisit;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
}