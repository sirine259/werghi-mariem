package com.hr.back.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    
    private Long id;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Account name is required")
    private String accountName;
    
    @NotNull(message = "Account type is required")
    private String accountType;
    
    @Min(value = 0, message = "Balance cannot be negative")
    private BigDecimal balance;
    
    private boolean active = true;
    
    @NotNull(message = "HR Manager ID is required")
    private Long hrManagerId;
    
    @NotNull(message = "Intermediary Client ID is required")
    private Long intermediaryClientId;
} 