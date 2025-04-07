package com.hr.back.service.impl;

import com.hr.back.model.Client;
import com.hr.back.model.Employee;
import com.hr.back.model.User;
import com.hr.back.repository.UserRepository;
import com.hr.back.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // New user, encrypt password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void toggleUserActive(Long id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = findById(user.getId());
        
        // Update user fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPhone(user.getPhone());
        existingUser.setAddress(user.getAddress());
        existingUser.setCin(user.getCin());
        existingUser.setRole(user.getRole());
        existingUser.setActive(user.isActive());
        
        // Don't update password if it's empty (meaning no password change)
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Employee> findAllEmployees() {
        return userRepository.findByRole(com.hr.back.model.UserRole.EMPLOYEE)
                .stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findAvailableEmployees() {
        return findAllEmployees().stream()
                .filter(Employee::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> findAllClients() {
        return userRepository.findByRole(com.hr.back.model.UserRole.CLIENT)
                .stream()
                .filter(user -> user instanceof Client)
                .map(user -> (Client) user)
                .collect(Collectors.toList());
    }

    @Override
    public List<Client> findClientsByType(Client.ClientType clientType) {
        return findAllClients().stream()
                .filter(client -> client.getClientType() == clientType)
                .collect(Collectors.toList());
    }
} 