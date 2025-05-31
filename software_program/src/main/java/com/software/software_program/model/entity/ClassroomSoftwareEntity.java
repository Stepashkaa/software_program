package com.software.software_program.model.entity;


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
@Table(name = "classroom_software")
public class ClassroomSoftwareEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomEntity classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id", nullable = false)
    private SoftwareEntity software;

    @Column(nullable = false)
    private LocalDate installationDate;

    public ClassroomSoftwareEntity(ClassroomEntity classroom, SoftwareEntity software, LocalDate installationDate) {
        this.classroom = classroom;
        this.software = software;
        this.installationDate = installationDate;
    }

}