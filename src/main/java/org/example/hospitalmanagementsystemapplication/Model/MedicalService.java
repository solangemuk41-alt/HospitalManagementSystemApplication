package org.example.hospitalmanagementsystemapplication.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "medical_services")
@EqualsAndHashCode(callSuper = true)
public class MedicalService extends AuditEntity {
    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToMany(mappedBy = "services")
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "service")
    private List<Appointment> appointments = new ArrayList<>();
}