package org.example.hospitalmanagementsystemapplication.Model;

import com.hospital.management.model.entity.enums.DoctorAvailability;
import com.hospital.management.model.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "doctors")
@EqualsAndHashCode(callSuper = true)
public class Doctor extends User {
    @Column(name = "medical_license_number", nullable = false, unique = true)
    private String medicalLicenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String specialisation;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private DoctorAvailability availabilityStatus = DoctorAvailability.AVAILABLE;

    @ManyToMany
    @JoinTable(
            name = "doctor_services",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<MedicalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    private List<Diagnosis> diagnoses = new ArrayList<>();
}