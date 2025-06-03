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

    List<NotificationEntity> findByIsRead(boolean isRead);

    List<NotificationEntity> findByUserIdAndIsRead(Long userId, boolean isRead);

    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);

    Page<NotificationEntity> findByIsRead(boolean isRead, Pageable pageable);

    Page<NotificationEntity> findByUserIdAndIsRead(Long userId, boolean isRead, Pageable pageable);
}
