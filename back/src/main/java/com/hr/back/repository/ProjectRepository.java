package com.hr.back.repository;

import com.hr.back.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManagerId(Long managerId);
    List<Project> findByClientId(Long clientId);
    List<Project> findByEmployeesId(Long employeeId);
    List<Project> findByStatus(String status);
    List<Project> findByActive(boolean active);
} 