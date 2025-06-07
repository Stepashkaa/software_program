package com.software.software_program.repository;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomSoftwareRepository extends JpaRepository<ClassroomSoftwareEntity, Long> {

    // Задача 6: Формирование обеспеченности аудитории ПО
    @Query("""
        SELECT cs FROM ClassroomSoftwareEntity cs
        JOIN FETCH cs.software
        JOIN FETCH cs.classroom
        WHERE cs.classroom.id = :classroomId
          AND cs.installationDate BETWEEN :start AND :end
        ORDER BY cs.software.name
    """)
    List<ClassroomSoftwareEntity> findClassroomSoftwareReport(
            @Param("classroomId") Long classroomId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Задача 7: Формирование списка ПО кафедры за период
    @Query("""
        SELECT cs FROM ClassroomSoftwareEntity cs
        JOIN FETCH cs.software
        JOIN FETCH cs.classroom c
        JOIN FETCH c.department
        WHERE c.department.id = :departmentId
          AND cs.installationDate BETWEEN :start AND :end
        ORDER BY c.name, cs.software.name
    """)
    List<ClassroomSoftwareEntity> findDepartmentSoftwareReport(
            @Param("departmentId") Long departmentId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Дополнительный метод для Task 7 (агрегированный список ПО без дубликатов)
    @Query("""
        SELECT DISTINCT cs.software FROM ClassroomSoftwareEntity cs
        JOIN cs.classroom c
        JOIN c.department d
        WHERE d.id = :departmentId
          AND cs.installationDate BETWEEN :start AND :end
        ORDER BY cs.software.name
    """)
    List<ClassroomSoftwareEntity> findUniqueSoftwareByDepartmentAndPeriod(
            @Param("departmentId") Long departmentId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    Optional<ClassroomSoftwareEntity> findByClassroomAndSoftware(ClassroomEntity classroom, SoftwareEntity software);

}