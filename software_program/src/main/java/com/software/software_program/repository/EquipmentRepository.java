package com.software.software_program.repository;

import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.ClassroomEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Long>, JpaSpecificationExecutor<EquipmentEntity> {
    Optional<EquipmentEntity> findBySerialNumberIgnoreCase(String serialNumber);

    List<EquipmentEntity> findByClassroom(@Param("classroom") ClassroomEntity classroom);

    @Query("""
        SELECT e FROM EquipmentEntity e
        JOIN e.classroom c
        WHERE (:type IS NULL OR LOWER(e.type) LIKE LOWER(CONCAT('%', :type, '%')))
          AND (:serialNumber IS NULL OR LOWER(e.serialNumber) LIKE LOWER(CONCAT('%', :serialNumber, '%')))
          AND (:classroomId IS NULL OR c.id = :classroomId)
    """)
    Page<EquipmentEntity> findAllByFilters(
            @Param("type") String type,
            @Param("serialNumber") String serialNumber,
            @Param("classroomId") Long classroomId,
            Pageable pageable
    );
}