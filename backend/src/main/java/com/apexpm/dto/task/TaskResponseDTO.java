package com.apexpm.dto.task;

import com.apexpm.domain.enumerator.TaskPriority;
import com.apexpm.domain.enumerator.TaskStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private Long projectId;
    private Long assigneeId;
    private String assigneeUsername;
}
