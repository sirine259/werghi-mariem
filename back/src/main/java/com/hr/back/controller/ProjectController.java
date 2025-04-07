package com.hr.back.controller;

import com.hr.back.dto.ProjectDTO;
import com.hr.back.model.Client;
import com.hr.back.model.Employee;
import com.hr.back.model.HRManager;
import com.hr.back.model.Project;
import com.hr.back.service.ProjectService;
import com.hr.back.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        Project project = convertToEntity(projectDTO);
        return new ResponseEntity<>(projectService.save(project), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        Project existingProject = projectService.findById(id);
        Project updated = updateEntityFromDTO(existingProject, projectDTO);
        return ResponseEntity.ok(projectService.save(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<Project> toggleProjectActive(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.toggleProjectActive(id));
    }

    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<List<Project>> getProjectsByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(projectService.findByManager(managerId));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER', 'ROLE_CLIENT')")
    public ResponseEntity<List<Project>> getProjectsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(projectService.findByClient(clientId));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<List<Project>> getProjectsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(projectService.findByEmployee(employeeId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(projectService.findByStatus(status));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<List<Project>> getActiveProjects() {
        return ResponseEntity.ok(projectService.findActiveProjects());
    }

    @PostMapping("/{projectId}/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<Project> addEmployeeToProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(projectService.addEmployeeToProject(projectId, employeeId));
    }

    @DeleteMapping("/{projectId}/employees/{employeeId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_HR_MANAGER')")
    public ResponseEntity<Project> removeEmployeeFromProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(projectService.removeEmployeeFromProject(projectId, employeeId));
    }

    private Project convertToEntity(ProjectDTO dto) {
        Project project = new Project();
        
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());
        project.setBudget(dto.getBudget());
        project.setActive(dto.isActive());
        
        // Set manager
        HRManager manager = (HRManager) userService.findById(dto.getManagerId());
        project.setManager(manager);
        
        // Set client if provided
        if (dto.getClientId() != null) {
            Client client = (Client) userService.findById(dto.getClientId());
            project.setClient(client);
        }
        
        // Set employees if provided
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            Set<Employee> employees = new HashSet<>();
            for (Long employeeId : dto.getEmployeeIds()) {
                Employee employee = (Employee) userService.findById(employeeId);
                employees.add(employee);
            }
            project.setEmployees(employees);
        }
        
        return project;
    }

    private Project updateEntityFromDTO(Project project, ProjectDTO dto) {
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus());
        project.setBudget(dto.getBudget());
        project.setActive(dto.isActive());
        
        // Update manager if changed
        if (!project.getManager().getId().equals(dto.getManagerId())) {
            HRManager manager = (HRManager) userService.findById(dto.getManagerId());
            project.setManager(manager);
        }
        
        // Update client if provided and changed
        if (dto.getClientId() != null) {
            if (project.getClient() == null || !project.getClient().getId().equals(dto.getClientId())) {
                Client client = (Client) userService.findById(dto.getClientId());
                project.setClient(client);
            }
        } else {
            project.setClient(null);
        }
        
        // Only completely replace employees if the DTO provides them
        if (dto.getEmployeeIds() != null) {
            Set<Employee> currentEmployees = project.getEmployees();
            Set<Long> currentEmployeeIds = currentEmployees.stream()
                    .map(Employee::getId)
                    .collect(Collectors.toSet());
            
            // Only update if there's a difference to avoid unnecessary database operations
            if (!currentEmployeeIds.equals(dto.getEmployeeIds())) {
                Set<Employee> newEmployees = new HashSet<>();
                for (Long employeeId : dto.getEmployeeIds()) {
                    Employee employee = (Employee) userService.findById(employeeId);
                    newEmployees.add(employee);
                }
                project.setEmployees(newEmployees);
            }
        }
        
        return project;
    }
} 