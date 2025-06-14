package com.software.software_program.repository;

import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentSoftwareRepository extends JpaRepository<EquipmentSoftwareEntity, Long> {

    /**
     * Отчет по программному обеспечению, установленному в конкретной аудитории за период
     */
//    @Query("""
//        SELECT DISTINCT s FROM EquipmentSoftwareEntity es
//        JOIN es.softwares s
//        JOIN es.equipment eq
//        JOIN eq.classroom c
//        WHERE c.id = :classroomId
//          AND es.installationDate BETWEEN :start AND :end
//        ORDER BY s.name
//    """)
//    List<SoftwareEntity> findClassroomSoftwareReport(
//            @Param("classroomId") Long classroomId,
//            @Param("start") LocalDate start,
//            @Param("end") LocalDate end
//    );

    /**
     * Отчет по программному обеспечению, установленному на кафедре (через аудитории и оборудование) за период
     */
    @Query("""
        SELECT DISTINCT es FROM EquipmentSoftwareEntity es
        JOIN FETCH es.softwares s
        JOIN FETCH es.equipment eq
        JOIN FETCH eq.classroom c
        JOIN FETCH c.department d
        WHERE d.id = :departmentId
          AND es.installationDate BETWEEN :start AND :end
        ORDER BY c.name, s.name
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
        SELECT DISTINCT s FROM EquipmentSoftwareEntity es
        JOIN es.softwares s
        JOIN es.equipment eq
        JOIN eq.classroom c
        JOIN c.department d
        WHERE d.id = :departmentId
          AND es.installationDate BETWEEN :start AND :end
        ORDER BY s.name
    """)
    List<SoftwareEntity> findUniqueSoftwareByDepartmentAndPeriod(
            @Param("departmentId") Long departmentId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );


    /**
     * Поиск записи по конкретному оборудованию и ПО
     */
    Optional<EquipmentSoftwareEntity> findByEquipmentAndSoftwaresContaining(
            EquipmentEntity equipment,
            SoftwareEntity software
    );

    List<EquipmentSoftwareEntity> findAllBySoftwaresContaining(SoftwareEntity software);

    List<EquipmentSoftwareEntity> findByEquipment(EquipmentEntity equipment);

    @Query("""
    SELECT e FROM EquipmentSoftwareEntity e
        JOIN FETCH e.equipment eq
        JOIN FETCH eq.classroom
        LEFT JOIN FETCH e.softwares s
        WHERE e.id = :id
    """)
    Optional<EquipmentSoftwareEntity> findByIdWithAllRelations(@Param("id") Long id);

    @Query("""
        DELETE FROM EquipmentSoftwareEntity es
        WHERE :software MEMBER OF es.softwares
    """)
    Optional<EquipmentSoftwareEntity> deleteAllBySoftware(@Param("software") SoftwareEntity software);

    @Query("""
        SELECT e FROM EquipmentSoftwareEntity e
        JOIN e.equipment eq
        WHERE LOWER(eq.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    Page<EquipmentSoftwareEntity> findByEquipmentNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);


}
