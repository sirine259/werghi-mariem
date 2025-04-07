package com.hr.back.controller;

import com.hr.back.dto.UserDTO;
import com.hr.back.model.Client;
import com.hr.back.model.Employee;
import com.hr.back.model.User;
import com.hr.back.model.UserRole;
import com.hr.back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = convertDTOToUser(userDTO);
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        User existingUser = userService.findById(id);
        User updatedUser = updateUserFromDTO(existingUser, userDTO);
        return ResponseEntity.ok(userService.updateUser(updatedUser));
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> toggleUserActive(@PathVariable Long id) {
        userService.toggleUserActive(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(userService.findAllEmployees());
    }

    @GetMapping("/employees/available")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<List<Employee>> getAvailableEmployees() {
        return ResponseEntity.ok(userService.findAvailableEmployees());
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(userService.findAllClients());
    }

    @GetMapping("/clients/final")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<List<Client>> getFinalClients() {
        return ResponseEntity.ok(userService.findClientsByType(Client.ClientType.FINAL));
    }

    @GetMapping("/clients/intermediary")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_HR_MANAGER')")
    public ResponseEntity<List<Client>> getIntermediaryClients() {
        return ResponseEntity.ok(userService.findClientsByType(Client.ClientType.INTERMEDIARY));
    }

    private User convertDTOToUser(UserDTO userDTO) {
        User user;
        
        switch (userDTO.getRole()) {
            case EMPLOYEE:
                Employee employee = new Employee();
                employee.setAvailable(userDTO.isAvailable());
                user = employee;
                break;
            case CLIENT:
                Client client = new Client();
                client.setClientType(userDTO.getClientType());
                user = client;
                break;
            default:
                user = new User();
        }
        
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setCin(userDTO.getCin());
        user.setRole(userDTO.getRole());
        user.setActive(userDTO.isActive());
        
        return user;
    }

    private User updateUserFromDTO(User existingUser, UserDTO userDTO) {
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setCin(userDTO.getCin());
        existingUser.setActive(userDTO.isActive());
        
        // Update type-specific fields
        if (existingUser instanceof Employee && userDTO.getRole() == UserRole.EMPLOYEE) {
            ((Employee) existingUser).setAvailable(userDTO.isAvailable());
        } else if (existingUser instanceof Client && userDTO.getRole() == UserRole.CLIENT) {
            ((Client) existingUser).setClientType(userDTO.getClientType());
        }
        
        // Only update password if it's provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(userDTO.getPassword());
        }
        
        return existingUser;
    }
} 