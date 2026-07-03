package com.shecancode.hospitalms.controller;

import com.shecancode.hospitalms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/appointments/daily")
    public ResponseEntity<Map<String, Object>> daily() {
        return ResponseEntity.ok(reportService.dailyAppointments());
    }

    @GetMapping("/appointments/weekly")
    public ResponseEntity<Map<String, Object>> weekly() {
        return ResponseEntity.ok(reportService.weeklyAppointments());
    }

    @GetMapping("/appointments/monthly")
    public ResponseEntity<Map<String, Object>> monthly() {
        return ResponseEntity.ok(reportService.monthlyAppointments());
    }

    @GetMapping("/patients/count")
    public ResponseEntity<Long> patientsCount() {
        return ResponseEntity.ok(reportService.numberOfPatients());
    }

    @GetMapping("/doctors/count")
    public ResponseEntity<Long> doctorsCount() {
        return ResponseEntity.ok(reportService.numberOfDoctors());
    }

    @GetMapping("/appointments/per-service")
    public ResponseEntity<Map<String, Long>> appointmentsPerService() {
        return ResponseEntity.ok(reportService.appointmentsPerService());
    }

    @GetMapping("/services/most-requested")
    public ResponseEntity<String> mostRequestedService() {
        return ResponseEntity.ok(reportService.mostRequestedService());
    }

    @GetMapping("/doctors/workload")
    public ResponseEntity<Map<String, Long>> doctorWorkload() {
        return ResponseEntity.ok(reportService.doctorWorkload());
    }

    @GetMapping("/appointments/cancelled")
    public ResponseEntity<Long> cancelledAppointments() {
        return ResponseEntity.ok(reportService.cancelledAppointments());
    }
}
