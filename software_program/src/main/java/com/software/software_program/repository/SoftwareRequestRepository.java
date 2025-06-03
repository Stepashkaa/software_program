package com.software.software_program.repository;

import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.model.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SoftwareRequestRepository extends JpaRepository<SoftwareRequestEntity, Long>, JpaSpecificationExecutor<SoftwareRequestEntity> {
    List<SoftwareRequestEntity> findByStatus(RequestStatus status);

    List<SoftwareRequestEntity> findByUser(UserEntity user);

    // Choose one of these two options:

    // Option 1: Use UserEntity
    List<SoftwareRequestEntity> findByStatusAndUser(RequestStatus status, UserEntity user);

    // OR Option 2: Use userId
    List<SoftwareRequestEntity> findByStatusAndUserId(RequestStatus status, Long userId);

    List<SoftwareRequestEntity> findByUserId(Long userId);

    @Query("""
        SELECT sr FROM SoftwareRequestEntity sr
        JOIN sr.user u
        JOIN sr.classroomSoftware cs
        WHERE (:status IS NULL OR sr.status = :status)
          AND (:userId IS NULL OR u.id = :userId)
          AND (:classroomSoftwareId IS NULL OR cs.id = :classroomSoftwareId)
    """)
    Page<SoftwareRequestEntity> findAllByFilters(
            @Param("status") RequestStatus status,
            @Param("userId") Long userId,
            @Param("classroomSoftwareId") Long classroomSoftwareId,
            Pageable pageable
    );
}
