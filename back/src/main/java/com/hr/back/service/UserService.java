package com.hr.back.service;

import com.hr.back.model.Client;
import com.hr.back.model.Employee;
import com.hr.back.model.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);
    User save(User user);
    void deleteById(Long id);
    User findById(Long id);
    boolean existsByEmail(String email);
    void toggleUserActive(Long id);
    User updateUser(User user);
    
    // New methods
    List<User> findAll();
    List<Employee> findAllEmployees();
    List<Employee> findAvailableEmployees();
    List<Client> findAllClients();
    List<Client> findClientsByType(Client.ClientType clientType);
} 