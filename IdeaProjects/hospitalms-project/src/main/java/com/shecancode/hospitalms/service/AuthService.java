package com.shecancode.hospitalms.service;

import com.shecancode.hospitalms.dto.auth.LoginRequest;
import com.shecancode.hospitalms.dto.auth.LoginResponse;
import com.shecancode.hospitalms.entity.Administrator;
import com.shecancode.hospitalms.entity.Doctor;
import com.shecancode.hospitalms.entity.Patient;
import com.shecancode.hospitalms.entity.enums.Role;
import com.shecancode.hospitalms.repository.AdministratorRepository;
import com.shecancode.hospitalms.repository.DoctorRepository;
import com.shecancode.hospitalms.repository.PatientRepository;
import com.shecancode.hospitalms.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AdministratorRepository administratorRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public LoginResponse login(LoginRequest request) {
        // Throws BadCredentialsException (handled globally) if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Determine which role table the user belongs to
        return administratorRepository.findByEmailIgnoreCase(request.getEmail())
                .map(admin -> buildResponse(admin, Role.ADMIN.name(), admin.getFullName()))
                .or(() -> doctorRepository.findByEmailIgnoreCase(request.getEmail())
                        .map(doc -> buildResponse(doc, Role.DOCTOR.name(), doc.getFullName())))
                .or(() -> patientRepository.findByEmailIgnoreCase(request.getEmail())
                        .map(pat -> buildResponse(pat, Role.PATIENT.name(), pat.getFullName())))
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in any role table"));
    }

    private LoginResponse buildResponse(UserDetails userDetails, String role, String fullName) {
        String token = jwtService.generateToken(userDetails, role);
        return LoginResponse.builder()
                .token(token)
                .email(userDetails.getUsername())
                .fullName(fullName)
                .role(role)
                .build();
    }
}
