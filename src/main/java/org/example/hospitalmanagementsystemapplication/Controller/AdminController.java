package org.example.hospitalmanagementsystemapplication.Controller;

import com.hospital.management.model.dto.response.DashboardStatsResponse;
import com.hospital.management.model.dto.response.SystemStatusResponse;
import com.hospital.management.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/system/health")
    public ResponseEntity<SystemStatusResponse> getSystemHealth() {
        SystemStatusResponse status = adminService.getSystemHealth();
        return ResponseEntity.ok(status);
    }


    @PostMapping("/cache/clear")
    public ResponseEntity<Void> clearCache() {
        adminService.clearCache();
        return ResponseEntity.ok().build();
    }


    @GetMapping("/logs")
    public ResponseEntity<String> getSystemLogs(
            @RequestParam(defaultValue = "100") int lines) {
        String logs = adminService.getSystemLogs(lines);
        return ResponseEntity.ok(logs);
    }
}