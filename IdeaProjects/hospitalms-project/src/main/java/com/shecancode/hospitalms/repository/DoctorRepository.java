package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByMedicalLicenseNumber(String medicalLicenseNumber);
    Optional<Doctor> findByEmailIgnoreCase(String email);
    List<Doctor> findByHospitalId(Long hospitalId);

    // Doctors under a given service that currently have fewer than 4 appointments on a date
    List<Doctor> findByServices_IdAndHospitalId(Long serviceId, Long hospitalId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT COUNT(a) FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
              AND a.status <> com.shecancode.hospitalms.entity.enums.AppointmentStatus.CANCELLED
              AND a.status <> com.shecancode.hospitalms.entity.enums.AppointmentStatus.REJECTED
            """)
    long countActiveAppointmentsForDoctorOnDate(Long doctorId, LocalDate date);
}
