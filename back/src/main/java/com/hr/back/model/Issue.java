package com.hr.back.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Column(length = 1000)
    private String justification;
    
    @Column(nullable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;
    
    public enum IssueType {
        TECHNICAL,
        SCHEDULE,
        RESOURCE,
        COMMUNICATION,
        OTHER
    }
} 