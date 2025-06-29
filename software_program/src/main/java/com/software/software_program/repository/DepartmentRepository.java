package com.software.software_program.repository;

import com.software.software_program.model.entity.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long>, JpaSpecificationExecutor<DepartmentEntity> {
    Optional<DepartmentEntity> findByNameIgnoreCase(@Param("name") String name);

    List<DepartmentEntity> findByNameContainingIgnoreCase(@Param("name") String name);

    Page<DepartmentEntity> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

}