package com.hr.back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "hr_managers")
public class HRManager extends User {
    
    public HRManager(Long id, String firstName, String lastName, String email, 
                   String password, String phone, String address, String cin, 
                   UserRole role, boolean active) {
        super(id, firstName, lastName, email, password, phone, address, cin, role, active);
    }
} 