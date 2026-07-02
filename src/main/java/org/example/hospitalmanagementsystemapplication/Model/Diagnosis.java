package org.example.hospitalmanagementsystemapplication.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "diagnoses")
@EqualsAndHashCode(callSuper = true)
public class Diagnosis extends AuditEntity {
    @Column(nullable = false)
    private String symptoms;

    @Column(name = "diagnosis_notes")
    private String diagnosisNotes;

    @Column(name = "recommended_treatment")
    private String recommendedTreatment;

    @Column(name = "prescription_file_path")
    private String prescriptionFilePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
}