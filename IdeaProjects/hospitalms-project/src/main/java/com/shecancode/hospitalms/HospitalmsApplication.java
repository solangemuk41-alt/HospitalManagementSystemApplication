package com.shecancode.hospitalms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class HospitalmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalmsApplication.class, args);
    }
}
