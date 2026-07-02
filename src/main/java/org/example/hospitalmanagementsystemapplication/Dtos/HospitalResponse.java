package org.example.hospitalmanagementsystemapplication.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponse {
    private Long id;
    private String name;
    private String telephone;
    private String physicalAddress;
    private String administratorName;
    private String administratorEmail;
    private Long administratorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}