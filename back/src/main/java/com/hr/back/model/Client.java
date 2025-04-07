package com.hr.back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client extends User {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClientType clientType;
    
    public Client(Long id, String firstName, String lastName, String email, 
                String password, String phone, String address, String cin, 
                UserRole role, boolean active, ClientType clientType) {
        super(id, firstName, lastName, email, password, phone, address, cin, role, active);
        this.clientType = clientType;
    }
    
    public enum ClientType {
        INTERMEDIARY,
        FINAL
    }
} 