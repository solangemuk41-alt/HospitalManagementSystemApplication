package com.shecancode.hospitalms.repository;

import com.shecancode.hospitalms.entity.Appointment;
import com.shecancode.hospitalms.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByDoctor_Hospital_Id(Long hospitalId);

    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
              AND a.appointmentTime = :time
              AND a.status NOT IN (com.shecancode.hospitalms.entity.enums.AppointmentStatus.CANCELLED,
                                    com.shecancode.hospitalms.entity.enums.AppointmentStatus.REJECTED)
            """)
    boolean existsOverlapForDoctor(Long doctorId, LocalDate date, LocalTime time);

    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.patient.id = :patientId
              AND a.appointmentDate = :date
              AND a.appointmentTime = :time
              AND a.status NOT IN (com.shecancode.hospitalms.entity.enums.AppointmentStatus.CANCELLED,
                                    com.shecancode.hospitalms.entity.enums.AppointmentStatus.REJECTED)
            """)
    boolean existsOverlapForPatient(Long patientId, LocalDate date, LocalTime time);

    long countByPatientIdAndStatusIn(Long patientId, List<AppointmentStatus> statuses);

    long countByDoctorIdAndAppointmentDateGreaterThanEqualAndStatusIn(
            Long doctorId, LocalDate date, List<AppointmentStatus> statuses);

    List<Appointment> findByAppointmentDateBetween(LocalDate start, LocalDate end);

    long countByStatus(AppointmentStatus status);

    long countByService_Id(Long serviceId);
}
