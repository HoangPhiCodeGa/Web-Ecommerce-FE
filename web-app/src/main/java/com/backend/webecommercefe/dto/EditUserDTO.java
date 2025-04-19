package com.backend.webecommercefe.dto;

import com.backend.webecommercefe.entities.Role;
import lombok.Data;

import java.util.List;

@Data
public class EditUserDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private List<Role> userRoles;
}