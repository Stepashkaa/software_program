package com.software.software_program.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "classroom_software")
public class ClassroomSoftwareEntity extends BaseEntity {
    @NaturalId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomEntity classroom;

    @NaturalId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id", nullable = false)
    private SoftwareEntity software;

    @Column(nullable = false)
    private Date installationDate;

    public ClassroomSoftwareEntity(ClassroomEntity classroom, SoftwareEntity software, Date installationDate) {
        this.classroom = classroom;
        this.software = software;
        this.installationDate = installationDate;
    }

}