package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.appointment.AppointmentReassignRequest;
import com.shecancode.hospitalms.dto.appointment.AppointmentRequest;
import com.shecancode.hospitalms.dto.appointment.AppointmentResponse;
import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.Appointment;
import com.shecancode.hospitalms.entity.Doctor;
import com.shecancode.hospitalms.entity.HospitalService;
import com.shecancode.hospitalms.entity.Patient;
import com.shecancode.hospitalms.entity.enums.AppointmentStatus;
import com.shecancode.hospitalms.exception.BusinessRuleException;
import com.shecancode.hospitalms.exception.ResourceNotFoundException;
import com.shecancode.hospitalms.exception.UnauthorizedActionException;
import com.shecancode.hospitalms.mapper.AppointmentMapper;
import com.shecancode.hospitalms.repository.AppointmentRepository;
import com.shecancode.hospitalms.repository.DoctorRepository;
import com.shecancode.hospitalms.repository.HospitalServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final int MAX_APPOINTMENTS_PER_DOCTOR_PER_DAY = 4;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalServiceRepository hospitalServiceRepository;
    private final AppointmentMapper appointmentMapper;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AppointmentResponse book(AppointmentRequest request) {
        Patient patient = currentUserProvider.currentPatient();

        HospitalService service = hospitalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (doctor.getServices().stream().noneMatch(s -> s.getId().equals(service.getId()))) {
            throw new BusinessRuleException("Selected doctor does not offer the selected service");
        }

        // Rule: max 4 appointments per doctor per day
        long doctorCountForDay = appointmentRepository.countActiveAppointmentsForDoctorOnDate(
                doctor.getId(), request.getAppointmentDate());
        if (doctorCountForDay >= MAX_APPOINTMENTS_PER_DOCTOR_PER_DAY) {
            throw new BusinessRuleException(
                    "Selected doctor already has 4 appointments on this date. Please choose another date/time.");
        }

        // Rule: appointment time cannot overlap for the same doctor
        if (appointmentRepository.existsOverlapForDoctor(doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime())) {
            throw new BusinessRuleException("Doctor already has an appointment at this date and time");
        }

        // Rule: a patient cannot have two appointments at the same date and time
        if (appointmentRepository.existsOverlapForPatient(patient.getId(), request.getAppointmentDate(), request.getAppointmentTime())) {
            throw new BusinessRuleException("You already have an appointment at this date and time");
        }

        Appointment appointment = Appointment.builder()
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reasonForVisit(request.getReasonForVisit())
                .status(AppointmentStatus.PENDING)
                .patient(patient)
                .doctor(doctor)
                .service(service)
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse approve(Long id) {
        Appointment appointment = getWithinAdminHospital(id);
        assertNotCompleted(appointment);
        appointment.setStatus(AppointmentStatus.APPROVED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse reject(Long id) {
        Appointment appointment = getWithinAdminHospital(id);
        assertNotCompleted(appointment);
        appointment.setStatus(AppointmentStatus.REJECTED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse reassign(Long id, AppointmentReassignRequest request) {
        Appointment appointment = getWithinAdminHospital(id);
        assertNotCompleted(appointment);

        Doctor newDoctor = doctorRepository.findById(request.getNewDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (newDoctor.getServices().stream().noneMatch(s -> s.getId().equals(appointment.getService().getId()))) {
            throw new BusinessRuleException("New doctor does not offer the appointment's service");
        }

        long doctorCountForDay = appointmentRepository.countActiveAppointmentsForDoctorOnDate(
                newDoctor.getId(), appointment.getAppointmentDate());
        if (doctorCountForDay >= MAX_APPOINTMENTS_PER_DOCTOR_PER_DAY) {
            throw new BusinessRuleException("New doctor already has 4 appointments on this date");
        }

        appointment.setDoctor(newDoctor);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse markCompleted(Long id) {
        // Called by the doctor once diagnosis is finalized
        Doctor doctor = currentUserProvider.currentDoctor();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new UnauthorizedActionException("You can only complete your own appointments");
        }
        assertNotCompleted(appointment);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        Patient patient = currentUserProvider.currentPatient();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new UnauthorizedActionException("You can only cancel your own appointments");
        }
        assertNotCompleted(appointment);
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())
                || appointment.getAppointmentDate().isEqual(LocalDate.now())) {
            throw new BusinessRuleException("Appointments may only be cancelled before the appointment date");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getMyAppointmentsAsPatient() {
        Patient patient = currentUserProvider.currentPatient();
        return appointmentRepository.findByPatientId(patient.getId())
                .stream().map(appointmentMapper::toResponse).toList();
    }

    public List<AppointmentResponse> getMyAppointmentsAsDoctor() {
        Doctor doctor = currentUserProvider.currentDoctor();
        return appointmentRepository.findByDoctorId(doctor.getId())
                .stream().map(appointmentMapper::toResponse).toList();
    }

    public List<AppointmentResponse> getAllForCurrentHospital() {
        Administrator admin = currentUserProvider.currentAdministrator();
        return appointmentRepository.findByDoctor_Hospital_Id(admin.getHospital().getId())
                .stream().map(appointmentMapper::toResponse).toList();
    }

    private void assertNotCompleted(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new BusinessRuleException("Completed appointments cannot be modified");
        }
    }

    private Appointment getWithinAdminHospital(Long id) {
        Administrator admin = currentUserProvider.currentAdministrator();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        if (!appointment.getDoctor().getHospital().getId().equals(admin.getHospital().getId())) {
            throw new ResourceNotFoundException("Appointment not found in your hospital");
        }
        return appointment;
    }
}
