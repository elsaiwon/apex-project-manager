package com.apexpm.dto.task;

import com.apexpm.domain.enumerator.TaskPriority;
import com.apexpm.domain.enumerator.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDTO {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private TaskStatus status;

    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    private Long assigneeId;
}