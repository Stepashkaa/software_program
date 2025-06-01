package com.software.software_program.repository;

import com.software.software_program.model.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>, JpaSpecificationExecutor<NotificationEntity> {
    List<NotificationEntity> findByUserId(Long userId);

    List<NotificationEntity> findByRead(boolean isRead);

    @Query("""
        SELECT n FROM NotificationEntity n
        WHERE (:userId IS NULL OR n.user.id = :userId)
          AND (:isRead IS NULL OR n.read = :isRead)
    """)
    Page<NotificationEntity> findAllByFilters(
            @Param("userId") Long userId,
            @Param("isRead") Boolean isRead,
            Pageable pageable
    );
}
