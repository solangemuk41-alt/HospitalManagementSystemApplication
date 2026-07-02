package org.example.hospitalmanagementsystemapplication.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "hospitals")
@EqualsAndHashCode(callSuper = true)
public class Hospital extends AuditEntity {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String telephone;

    @Column(name = "physical_address", nullable = false)
    private String physicalAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "administrator_id")
    private Administrator administrator;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<MedicalService> services = new ArrayList<>();

    @OneToMany(mappedBy = "hospital")
    private List<Patient> patients = new ArrayList<>();

    @OneToMany(mappedBy = "hospital")
    private List<Appointment> appointments = new ArrayList<>();
}