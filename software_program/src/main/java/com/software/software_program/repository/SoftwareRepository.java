package com.software.software_program.repository;

import com.software.software_program.model.entity.ClassroomEntity;
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
    Optional<SoftwareEntity> findByNameIgnoreCase(@Param("name") String name);

    @Query("""
        SELECT c FROM SoftwareEntity c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
           OR LOWER(:name) LIKE LOWER(CONCAT('%', c.name, '%'))
    """)
    List<SoftwareEntity> findByNameContainingIgnoreCase(@Param("name") String name);


    Page<SoftwareEntity> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);


    List<SoftwareEntity> findByVersionContainingIgnoreCase(String version);

    @Query("""
      SELECT s FROM SoftwareEntity s
      LEFT JOIN FETCH s.equipmentSoftwares es
      LEFT JOIN FETCH es.equipment
      WHERE s.id = :id
    """)
    Optional<SoftwareEntity> findByIdWithRelations(@Param("id") Long id);

}