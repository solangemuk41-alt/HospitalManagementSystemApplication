package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.Appointment;
import com.shecancode.hospitalms.entity.enums.AppointmentStatus;
import com.shecancode.hospitalms.repository.AppointmentRepository;
import com.shecancode.hospitalms.repository.DoctorRepository;
import com.shecancode.hospitalms.repository.HospitalServiceRepository;
import com.shecancode.hospitalms.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalServiceRepository hospitalServiceRepository;
    private final CurrentUserProvider currentUserProvider;

    public Map<String, Object> dailyAppointments() {
        return appointmentsBetween(LocalDate.now(), LocalDate.now());
    }

    public Map<String, Object> weeklyAppointments() {
        LocalDate start = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);
        return appointmentsBetween(start, end);
    }

    public Map<String, Object> monthlyAppointments() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return appointmentsBetween(start, end);
    }

    private Map<String, Object> appointmentsBetween(LocalDate start, LocalDate end) {
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(start, end)
                .stream()
                .filter(a -> a.getDoctor().getHospital().getId().equals(currentHospitalId()))
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("from", start);
        result.put("to", end);
        result.put("totalAppointments", appointments.size());
        return result;
    }

    public long numberOfPatients() {
        // Note: patients are not scoped to a single hospital in this schema (they can book anywhere),
        // so this returns the platform-wide patient count.
        return patientRepository.count();
    }

    public long numberOfDoctors() {
        Administrator admin = currentUserProvider.currentAdministrator();
        return doctorRepository.findByHospitalId(admin.getHospital().getId()).size();
    }

    public Map<String, Long> appointmentsPerService() {
        Administrator admin = currentUserProvider.currentAdministrator();
        return hospitalServiceRepository.findByHospitalId(admin.getHospital().getId()).stream()
                .collect(Collectors.toMap(
                        s -> s.getName(),
                        s -> appointmentRepository.countByService_Id(s.getId()),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    public String mostRequestedService() {
        Map<String, Long> counts = appointmentsPerService();
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No data available");
    }

    public Map<String, Long> doctorWorkload() {
        Administrator admin = currentUserProvider.currentAdministrator();
        return doctorRepository.findByHospitalId(admin.getHospital().getId()).stream()
                .collect(Collectors.toMap(
                        d -> d.getFullName(),
                        d -> (long) appointmentRepository.findByDoctorId(d.getId()).size(),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    public long cancelledAppointments() {
        return appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
    }

    private Long currentHospitalId() {
        return currentUserProvider.currentAdministrator().getHospital().getId();
    }
}
