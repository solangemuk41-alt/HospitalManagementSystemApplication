package com.shecancode.hospitalms.dto.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisResponse {
    private Long id;
    private String symptoms;
    private String diagnosisNotes;
    private String prescriptionFilePath;
    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private boolean readOnly;
}
