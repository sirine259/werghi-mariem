package com.hr.back.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProjectDTO {
    
    private Long id;
    
    @NotBlank(message = "Project name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotNull(message = "Budget is required")
    @Min(value = 0, message = "Budget cannot be negative")
    private BigDecimal budget;
    
    private Long clientId;
    
    @NotNull(message = "Manager ID is required")
    private Long managerId;
    
    private Set<Long> employeeIds = new HashSet<>();
    
    private boolean active = true;
} 