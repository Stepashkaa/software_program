package com.software.software_program.repository;

import com.software.software_program.model.entity.SoftwareEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SoftwareRepository extends JpaRepository<SoftwareEntity, Long>, JpaSpecificationExecutor<SoftwareEntity> {
    Optional<SoftwareEntity> findByNameIgnoreCase(String name);

    List<SoftwareEntity> findByNameContainingIgnoreCase(String name);

    Page<SoftwareEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
        SELECT s FROM SoftwareEntity s
        WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:version IS NULL OR LOWER(s.version) LIKE LOWER(CONCAT('%', :version, '%')))
          AND (:description IS NULL OR LOWER(s.description) LIKE LOWER(CONCAT('%', :description, '%')))
    """)
    Page<SoftwareEntity> findAllByFilters(
            @Param("name") String name,
            @Param("version") String version,
            @Param("description") String description,
            Pageable pageable
    );
}