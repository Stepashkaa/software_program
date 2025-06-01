package com.software.software_program.repository;

import com.software.software_program.model.entity.ClassroomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<ClassroomEntity, Long>, JpaSpecificationExecutor<ClassroomEntity> {
    Optional<ClassroomEntity> findByNameIgnoreCase(String name);

    List<ClassroomEntity> findByNameContainingIgnoreCase(String name);

    @Query("""
        SELECT c FROM ClassroomEntity c
        JOIN c.department d
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:capacity IS NULL OR c.capacity >= :capacity)
          AND (:departmentId IS NULL OR d.id = :departmentId)
    """)
    Page<ClassroomEntity> findAllByFilters(
            @Param("name") String name,
            @Param("capacity") Integer capacity,
            @Param("departmentId") Long departmentId,
            Pageable pageable
    );
}
