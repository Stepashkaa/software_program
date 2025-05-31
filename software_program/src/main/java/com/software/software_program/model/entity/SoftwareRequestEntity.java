package com.software.software_program.model.entity;

import com.software.software_program.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "software_request")
public class SoftwareRequestEntity extends BaseEntity {
    @Column(nullable = false)
    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_software_id", nullable = false)
    private ClassroomSoftwareEntity classroomSoftware;

    public SoftwareRequestEntity(LocalDate requestDate, RequestStatus status, String description, UserEntity user, ClassroomSoftwareEntity classroomSoftware) {
        this.requestDate = requestDate;
        this.status = status;
        this.description = description;
        this.user = user;
        this.classroomSoftware = classroomSoftware;
    }
}
