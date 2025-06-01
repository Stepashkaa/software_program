package com.software.software_program.repository;

import com.software.software_program.model.entity.FacultyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends JpaRepository<FacultyEntity, Long>, JpaSpecificationExecutor<FacultyEntity> {
    Optional<FacultyEntity> findByNameIgnoreCase(String name);

    List<FacultyEntity> findByNameContainingIgnoreCase(String name);

    Page<FacultyEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
