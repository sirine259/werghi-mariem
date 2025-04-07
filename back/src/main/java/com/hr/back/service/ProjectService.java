package com.hr.back.service;

import com.hr.back.model.Project;

import java.util.List;

public interface ProjectService {
    List<Project> findAll();
    Project findById(Long id);
    Project save(Project project);
    void deleteById(Long id);
    List<Project> findByManager(Long managerId);
    List<Project> findByClient(Long clientId);
    List<Project> findByEmployee(Long employeeId);
    List<Project> findByStatus(String status);
    List<Project> findActiveProjects();
    Project toggleProjectActive(Long id);
    Project addEmployeeToProject(Long projectId, Long employeeId);
    Project removeEmployeeFromProject(Long projectId, Long employeeId);
} 