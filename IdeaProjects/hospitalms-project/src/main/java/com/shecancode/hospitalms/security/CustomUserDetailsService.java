package com.shecancode.hospitalms.security;

import com.shecancode.hospitalms.repository.AdministratorRepository;
import com.shecancode.hospitalms.repository.DoctorRepository;
import com.shecancode.hospitalms.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Looks up the logged-in user by email across the three role tables
 * (Administrator, Doctor, Patient) since there is no single "users" table.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministratorRepository administratorRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return administratorRepository.findByEmailIgnoreCase(email)
                .map(UserDetails.class::cast)
                .or(() -> doctorRepository.findByEmailIgnoreCase(email).map(UserDetails.class::cast))
                .or(() -> patientRepository.findByEmailIgnoreCase(email).map(UserDetails.class::cast))
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
    }
}
