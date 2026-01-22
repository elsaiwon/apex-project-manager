package com.apexpm.service;

import com.apexpm.domain.entity.ApexProject;
import com.apexpm.domain.entity.ApexUser;
import com.apexpm.domain.enumerator.Role;
import com.apexpm.dto.project.ProjectRequestDTO;
import com.apexpm.dto.project.ProjectResponseDTO;
import com.apexpm.repository.ApexProjectRepository;
import com.apexpm.repository.ApexUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired private ApexProjectRepository projectRepository;
    @Autowired private ApexUserRepository userRepository;

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        ApexUser currentUser = (ApexUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ApexProject project = ApexProject.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .manager(currentUser)
                .build();

        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getMyProjects() {
        ApexUser currentUser = (ApexUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ApexProject> projects = projectRepository.findByManagerId(currentUser.getId());
        projects.addAll(projectRepository.findByMembersId(currentUser.getId()));
        return projects.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long projectId) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato con ID: " + projectId));

        ApexUser currentUser = getCurrentUser();

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !project.getMembers().stream().anyMatch(u -> u.getId().equals(currentUser.getId()))) {
            throw new SecurityException("Non autorizzato a visualizzare questo progetto");
        }

        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponseDTO updateProject(Long projectId, ProjectRequestDTO dto) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato a modificare questo progetto");
        }

        project.setName(dto.getName());
        project.setDescription(dto.getDescription());

        project = projectRepository.save(project);
        return mapToResponse(project);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato a eliminare questo progetto");
        }

        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponseDTO addMember(Long projectId, Long userId) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato"));

        ApexUser currentUser = getCurrentUser();
        ApexUser memberToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato ad aggiungere membri");
        }

        project.getMembers().add(memberToAdd);
        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponseDTO removeMember(Long projectId, Long userId) {
        ApexProject project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Progetto non trovato"));

        ApexUser currentUser = getCurrentUser();

        if (!project.getManager().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new SecurityException("Non autorizzato a rimuovere membri");
        }

        project.getMembers().removeIf(u -> u.getId().equals(userId));
        project = projectRepository.save(project);

        return mapToResponse(project);
    }

    private ApexUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("Utente non autenticato");
        }
        return (ApexUser) auth.getPrincipal();
    }

    private ProjectResponseDTO mapToResponse(ApexProject p) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setManagerId(p.getManager().getId());
        dto.setManagerUsername(p.getManager().getUsername());
        dto.setMemberIds(p.getMembers().stream().map(ApexUser::getId).collect(Collectors.toSet()));
        dto.setTaskCount(p.getTasks().size());
        return dto;
    }

}