package com.apexpm.service;

import com.apexpm.domain.entity.ApexProject;
import com.apexpm.domain.entity.ApexTask;
import com.apexpm.domain.entity.ApexUser;
import com.apexpm.domain.enumerator.Role;
import com.apexpm.domain.enumerator.TaskStatus;
import com.apexpm.dto.task.TaskRequestDTO;
import com.apexpm.dto.task.TaskResponseDTO;
import com.apexpm.repository.ApexProjectRepository;
import com.apexpm.repository.ApexTaskRepository;
import com.apexpm.repository.ApexUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired private ApexTaskRepository taskRepository;
    @Autowired private ApexProjectRepository projectRepository;
    @Autowired private ApexUserRepository userRepository;

    @Transactional
    public TaskResponseDTO createTask(Long projectId, TaskRequestDTO dto) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato a creare task in questo progetto");
        }

        ApexUser assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assegnatario non trovato"));
            if (!project.getMembers().contains(assignee) && !project.getManager().equals(assignee)) {
                throw new IllegalArgumentException("L'assegnatario deve essere membro del progetto");
            }
        }

        ApexTask task = ApexTask.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate())
                .project(project)
                .assignee(assignee)
                .build();

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByProject(Long projectId) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !project.getMembers().stream().anyMatch(u -> u.getId().equals(currentUser.getId()))) {
            throw new SecurityException("Non autorizzato a visualizzare i task di questo progetto");
        }

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponseDTO updateTaskStatus(Long taskId, TaskStatus newStatus) {
        ApexTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!task.getAssignee().getId().equals(currentUser.getId()) &&
                !task.getProject().getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato a aggiornare lo status di questo task");
        }

        task.setStatus(newStatus);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        ApexTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!task.getProject().getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato a eliminare questo task");
        }

        taskRepository.delete(task);
    }

    private TaskResponseDTO mapToResponse(ApexTask t) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(t.getId());
        dto.setTitle(t.getTitle());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setPriority(t.getPriority());
        dto.setDueDate(t.getDueDate());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setProjectId(t.getProject().getId());
        if (t.getAssignee() != null) {
            dto.setAssigneeId(t.getAssignee().getId());
            dto.setAssigneeUsername(t.getAssignee().getUsername());
        }
        return dto;
    }

    private ApexUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Utente non autenticato");
        }
        return (ApexUser) auth.getPrincipal();
    }
}