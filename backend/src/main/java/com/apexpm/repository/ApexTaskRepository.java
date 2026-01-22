package com.apexpm.repository;

import com.apexpm.domain.entity.ApexTask;
import com.apexpm.domain.enumerator.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApexTaskRepository extends JpaRepository<ApexTask, Long> {
    List<ApexTask> findByProjectId(Long projectId);
    List<ApexTask> findByAssigneeId(Long assigneeId);
    List<ApexTask> findByStatus(TaskStatus status);
}