package com.software.software_program.model.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.Check;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "equipment")
public class EquipmentEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "serial_number", nullable = false, unique = true, length = 50)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = true)
    private ClassroomEntity classroom;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EquipmentSoftwareEntity> equipmentSoftwares = new HashSet<>();

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SoftwareRequestEntity> softwareRequests = new HashSet<>();

    public EquipmentEntity(String name, String type, String serialNumber, ClassroomEntity classroom) {
        this.name = name;
        this.type = type;
        this.serialNumber = serialNumber;
        this.classroom = classroom;
    }

    public void addSoftware(EquipmentSoftwareEntity software) {
        if (software != null) {
            equipmentSoftwares.add(software);
            software.setEquipment(this);
        }
    }

    public void removeSoftware(EquipmentSoftwareEntity software) {
        if (software != null) {
            equipmentSoftwares.remove(software);
            software.setEquipment(null);
        }
    }

    public void addSoftwareRequest(SoftwareRequestEntity request) {
        if (request != null) {
            softwareRequests.add(request);
            request.setEquipment(this);
        }
    }

    public void removeSoftwareRequest(SoftwareRequestEntity request) {
        if (request != null) {
            softwareRequests.remove(request);
            request.setEquipment(null);
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
        EquipmentEntity that = (EquipmentEntity) o;
        return Objects.equals(id, that.id);
    }

}