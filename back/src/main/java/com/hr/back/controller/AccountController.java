package com.hr.back.controller;

import com.hr.back.dto.AccountDTO;
import com.hr.back.model.Account;
import com.hr.back.model.Client;
import com.hr.back.model.HRManager;
import com.hr.back.service.AccountService;
import com.hr.back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        HRManager hrManager = (HRManager) userService.findById(accountDTO.getHrManagerId());
        Client intermediaryClient = (Client) userService.findById(accountDTO.getIntermediaryClientId());
        
        // Verify client type is INTERMEDIARY
        if (intermediaryClient.getClientType() != Client.ClientType.INTERMEDIARY) {
            return ResponseEntity.badRequest().build();
        }
        
        Account account = new Account();
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setAccountName(accountDTO.getAccountName());
        account.setAccountType(accountDTO.getAccountType());
        account.setBalance(accountDTO.getBalance());
        account.setActive(accountDTO.isActive());
        account.setHrManager(hrManager);
        account.setIntermediaryClient(intermediaryClient);
        
        return new ResponseEntity<>(accountService.save(account), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDTO accountDTO) {
        Account account = accountService.findById(id);
        
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setAccountName(accountDTO.getAccountName());
        account.setAccountType(accountDTO.getAccountType());
        account.setBalance(accountDTO.getBalance());
        account.setActive(accountDTO.isActive());
        
        // Update client and HR manager only if they're changed
        if (accountDTO.getHrManagerId() != null && 
            !account.getHrManager().getId().equals(accountDTO.getHrManagerId())) {
            HRManager hrManager = (HRManager) userService.findById(accountDTO.getHrManagerId());
            account.setHrManager(hrManager);
        }
        
        if (accountDTO.getIntermediaryClientId() != null && 
            !account.getIntermediaryClient().getId().equals(accountDTO.getIntermediaryClientId())) {
            Client intermediaryClient = (Client) userService.findById(accountDTO.getIntermediaryClientId());
            
            // Verify client type is INTERMEDIARY
            if (intermediaryClient.getClientType() != Client.ClientType.INTERMEDIARY) {
                return ResponseEntity.badRequest().build();
            }
            
            account.setIntermediaryClient(intermediaryClient);
        }
        
        return ResponseEntity.ok(accountService.save(account));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Account> toggleAccountActive(@PathVariable Long id) {
        Account account = accountService.findById(id);
        account.setActive(!account.isActive());
        return ResponseEntity.ok(accountService.save(account));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hr-manager/{hrManagerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<List<Account>> getAccountsByHRManager(@PathVariable Long hrManagerId) {
        return ResponseEntity.ok(accountService.findByHRManager(hrManagerId));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<List<Account>> getAccountsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(accountService.findByIntermediaryClient(clientId));
    }
} 