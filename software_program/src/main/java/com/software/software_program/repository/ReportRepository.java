package com.software.software_program.repository;

import com.software.software_program.model.entity.ReportEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long>, JpaSpecificationExecutor<ReportEntity> {
    List<ReportEntity> findByGeneratedDateBetween(LocalDate startDate, LocalDate endDate);

    List<ReportEntity> findByDepartment(DepartmentEntity department);

    @Query("""
        SELECT r FROM ReportEntity r
        JOIN r.department d
        WHERE (:startDate IS NULL OR r.generatedDate >= :startDate)
          AND (:endDate IS NULL OR r.generatedDate <= :endDate)
          AND (:departmentId IS NULL OR d.id = :departmentId)
    """)
    Page<ReportEntity> findAllByFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("departmentId") Long departmentId,
            Pageable pageable
    );
}