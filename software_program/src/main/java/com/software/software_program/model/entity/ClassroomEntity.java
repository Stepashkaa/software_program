package com.software.software_program.model.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
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
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EquipmentEntity> equipments = new HashSet<>();

    public ClassroomEntity(String name, Integer capacity, DepartmentEntity department) {
        this.name = name;
        this.capacity = capacity;
        this.department = department;
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

    @Override
    public int hashCode() {
        return Objects.hash(id); // Используем только id
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassroomEntity that = (ClassroomEntity) o;
        return Objects.equals(id, that.id);
    }
}