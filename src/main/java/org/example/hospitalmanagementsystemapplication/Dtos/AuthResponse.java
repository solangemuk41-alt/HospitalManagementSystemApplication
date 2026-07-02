package org.example.hospitalmanagementsystemapplication.Dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String fullName;
    private Long userId;
}