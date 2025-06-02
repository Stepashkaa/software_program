package com.software.software_program.repository;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassroomSoftwareRepository extends JpaRepository<ClassroomSoftwareEntity, Long>, JpaSpecificationExecutor<ClassroomSoftwareEntity> {
    List<ClassroomSoftwareEntity> findByClassroom(ClassroomEntity classroom);

    @Query("""
        SELECT cs FROM ClassroomSoftwareEntity cs
        JOIN cs.classroom c
        JOIN cs.software s
        WHERE (:classroomId IS NULL OR c.id = :classroomId)
          AND (:softwareId IS NULL OR s.id = :softwareId)
    """)
    Page<ClassroomSoftwareEntity> findAllByFilters(
            @Param("classroomId") Long classroomId,
            @Param("softwareId") Long softwareId,
            Pageable pageable
    );
}