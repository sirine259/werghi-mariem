package com.hr.back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "employees")
public class Employee extends User {
    @Column(nullable = false)
    private boolean available = true;
    
    public Employee(Long id, String firstName, String lastName, String email, 
                   String password, String phone, String address, String cin, 
                   UserRole role, boolean active, boolean available) {
        super(id, firstName, lastName, email, password, phone, address, cin, role, active);
        this.available = available;
    }
} 