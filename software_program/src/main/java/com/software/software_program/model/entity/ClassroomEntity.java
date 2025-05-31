package com.software.software_program.model.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Check;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "classroom")
public class ClassroomEntity extends BaseEntity {
    @Check(constraints = "length(name) >= 1")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassroomSoftwareEntity> classroomSoftwares = new HashSet<>();

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EquipmentEntity> equipments = new HashSet<>();

    public ClassroomEntity(String name, Integer capacity, DepartmentEntity department) {
        this.name = name;
        this.capacity = capacity;
        this.department = department;
    }

    public void addClassroomSoftware(ClassroomSoftwareEntity classroomSoftware) {
        if (classroomSoftware != null) {
            classroomSoftwares.add(classroomSoftware);
            classroomSoftware.setClassroom(this);
        }
    }

    public void removeClassroomSoftware(ClassroomSoftwareEntity classroomSoftware) {
        if (classroomSoftware != null) {
            classroomSoftwares.remove(classroomSoftware);
            classroomSoftware.setClassroom(null);
        }
    }

    public void addEquipment(EquipmentEntity equipment) {
        if (equipment != null) {
            equipments.add(equipment);
            equipment.setClassroom(this);
        }
    }

    public void removeEquipment(EquipmentEntity equipment) {
        if (equipment != null) {
            equipments.remove(equipment);
            equipment.setClassroom(null);
        }
    }
}