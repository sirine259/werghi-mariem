package com.hr.back.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();
    
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private String status = "PLANNING"; // PLANNING, IN_PROGRESS, COMPLETED, ON_HOLD, CANCELLED
    
    @Column(nullable = false)
    private BigDecimal budget = BigDecimal.ZERO;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private HRManager manager;
    
    @ManyToMany
    @JoinTable(
        name = "project_employees",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();
    
    @Column(nullable = false)
    private boolean active = true;
} 