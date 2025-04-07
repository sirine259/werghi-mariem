package com.hr.back.service.impl;

import com.hr.back.model.Employee;
import com.hr.back.model.Project;
import com.hr.back.repository.ProjectRepository;
import com.hr.back.service.ProjectService;
import com.hr.back.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public List<Project> findByManager(Long managerId) {
        return projectRepository.findByManagerId(managerId);
    }

    @Override
    public List<Project> findByClient(Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    @Override
    public List<Project> findByEmployee(Long employeeId) {
        return projectRepository.findByEmployeesId(employeeId);
    }

    @Override
    public List<Project> findByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    @Override
    public List<Project> findActiveProjects() {
        return projectRepository.findByActive(true);
    }

    @Override
    public Project toggleProjectActive(Long id) {
        Project project = findById(id);
        project.setActive(!project.isActive());
        return projectRepository.save(project);
    }

    @Override
    public Project addEmployeeToProject(Long projectId, Long employeeId) {
        Project project = findById(projectId);
        Employee employee = (Employee) userService.findById(employeeId);
        
        if (employee == null) {
            throw new EntityNotFoundException("Employee not found with id: " + employeeId);
        }
        
        project.getEmployees().add(employee);
        return projectRepository.save(project);
    }

    @Override
    public Project removeEmployeeFromProject(Long projectId, Long employeeId) {
        Project project = findById(projectId);
        project.getEmployees().removeIf(employee -> employee.getId().equals(employeeId));
        return projectRepository.save(project);
    }
} 