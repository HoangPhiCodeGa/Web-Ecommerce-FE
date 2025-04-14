package com.backend.webecommercefe.dto;

import com.backend.webecommercefe.entities.Role;
import java.util.List;

public class EditUserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private List<Role> userRoles;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<Role> getUserRoles() { return userRoles; }
    public void setUserRoles(List<Role> userRoles) { this.userRoles = userRoles; }
}