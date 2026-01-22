package com.apexpm.repository;

import com.apexpm.domain.entity.ApexUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApexUserRepository extends JpaRepository<ApexUser, Long> {

    Optional<ApexUser> findByUsername(String username);

    Optional<ApexUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}