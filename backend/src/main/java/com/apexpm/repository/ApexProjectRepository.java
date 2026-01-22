package com.apexpm.repository;

import com.apexpm.domain.entity.ApexProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApexProjectRepository extends JpaRepository<ApexProject, Long> {
    List<ApexProject> findByManagerId(Long managerId);
    List<ApexProject> findByMembersId(Long userId);
}
