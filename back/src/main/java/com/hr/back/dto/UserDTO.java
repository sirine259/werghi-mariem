package com.hr.back.dto;

import com.hr.back.model.Client;
import com.hr.back.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private String password;
    
    private String phone;
    
    private String address;
    
    private String cin;
    
    @NotNull(message = "Role is required")
    private UserRole role;
    
    private boolean active = true;
    
    // For Employee
    private boolean available = true;
    
    // For Client
    private Client.ClientType clientType;
} 