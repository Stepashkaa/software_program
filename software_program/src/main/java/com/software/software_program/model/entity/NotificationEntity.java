package com.software.software_program.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notification")
public class NotificationEntity extends BaseEntity {
    @Column(nullable = false, length = 200)
    private String message;

    @Column(nullable = false)
    private LocalDateTime sentDate;

    @Column(name = "is_read", nullable = false)
    private boolean isRead; // Альтернативный вариант

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public NotificationEntity(String message, LocalDateTime sentDate, UserEntity user, boolean isRead) {
        this.message = message;
        this.sentDate = sentDate;
        this.user = user;
        this.isRead = isRead;
    }
}