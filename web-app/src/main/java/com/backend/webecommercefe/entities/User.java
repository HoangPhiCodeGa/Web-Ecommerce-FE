package com.backend.webecommercefe.entities;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Boolean gender;
    private String email;
}