package com.software.software_program.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "equipment_software")
public class EquipmentSoftwareEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentEntity equipment;

    @ManyToMany
    @JoinTable(
            name = "equipment_software_mapping",
            joinColumns = @JoinColumn(name = "equipment_software_id"),
            inverseJoinColumns = @JoinColumn(name = "software_id")
    )
    private Set<SoftwareEntity> softwares = new HashSet<>();

    @Column(nullable = false)
    private Date installationDate;

    public EquipmentSoftwareEntity(EquipmentEntity equipment, Set<SoftwareEntity> softwares, Date installationDate) {
        this.equipment = equipment;
        this.softwares = softwares;
        this.installationDate = installationDate;
    }

    public void addSoftware(SoftwareEntity software) {
        softwares.add(software);
    }

    public void removeSoftware(SoftwareEntity software) {
        softwares.remove(software);
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