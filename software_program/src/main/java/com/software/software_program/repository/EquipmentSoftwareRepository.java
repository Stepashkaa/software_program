package com.software.software_program.repository;

import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentSoftwareRepository extends JpaRepository<EquipmentSoftwareEntity, Long> {

    /**
     * Отчет по программному обеспечению, установленному в конкретной аудитории за период
     */
    @Query("""
        SELECT es FROM EquipmentSoftwareEntity es
        JOIN FETCH es.software
        JOIN FETCH es.equipment eq
        JOIN FETCH eq.classroom c
        WHERE c.id = :classroomId
          AND es.installationDate BETWEEN :start AND :end
        ORDER BY es.software.name
    """)
    List<EquipmentSoftwareEntity> findClassroomSoftwareReport(
            @Param("classroomId") Long classroomId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * Отчет по программному обеспечению, установленному на кафедре (через аудитории и оборудование) за период
     */
    @Query("""
        SELECT es FROM EquipmentSoftwareEntity es
        JOIN FETCH es.software
        JOIN FETCH es.equipment eq
        JOIN FETCH eq.classroom c
        JOIN FETCH c.department d
        WHERE d.id = :departmentId
          AND es.installationDate BETWEEN :start AND :end
        ORDER BY c.name, es.software.name
    """)
    List<EquipmentSoftwareEntity> findDepartmentSoftwareReport(
            @Param("departmentId") Long departmentId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * Уникальное ПО, использовавшееся на кафедре за период (без повторений)
     */
    @Query("""
        SELECT DISTINCT es.software FROM EquipmentSoftwareEntity es
        JOIN es.equipment eq
        JOIN eq.classroom c
        JOIN c.department d
        WHERE d.id = :departmentId
          AND es.installationDate BETWEEN :start AND :end
        ORDER BY es.software.name
    """)
    List<SoftwareEntity> findUniqueSoftwareByDepartmentAndPeriod(
            @Param("departmentId") Long departmentId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * Поиск записи по конкретному оборудованию и ПО
     */
    Optional<EquipmentSoftwareEntity> findByEquipmentAndSoftware(
            EquipmentEntity equipment,
            SoftwareEntity software
    );
}
