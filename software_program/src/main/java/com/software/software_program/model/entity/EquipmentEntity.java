package com.software.software_program.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
@Table(name = "equipment")
public class EquipmentEntity extends BaseEntity {
    @Check(constraints = "length(name) >= 1")
    @Column(nullable = false, length = 50)
    private String name;

    @Check(constraints = "length(type) >= 1")
    @Column(nullable = false, length = 50)
    private String type;

    @Check(constraints = "length(serial_number) >= 1")
    @Column(name = "serial_number", nullable = false, unique = true, length = 50)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private ClassroomEntity classroom;

    public EquipmentEntity(String name, String type, String serialNumber, ClassroomEntity classroom) {
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.classroom = classroom;
    }
}