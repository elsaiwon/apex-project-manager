package com.apexpm.dto.project;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private Long managerId;
    private String managerUsername;
    private Set<Long> memberIds;
    private int taskCount; // bonus
}