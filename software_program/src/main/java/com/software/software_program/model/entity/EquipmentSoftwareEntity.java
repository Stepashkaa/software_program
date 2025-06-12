package com.software.software_program.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NaturalId;

import java.util.Date;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id", nullable = false)
    private SoftwareEntity software;

    @Column(nullable = false)
    private Date installationDate;

    public EquipmentSoftwareEntity(EquipmentEntity equipment, SoftwareEntity software, Date installationDate) {
        this.equipment = equipment;
        this.software = software;
        this.installationDate = installationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipmentSoftwareEntity)) return false;
        EquipmentSoftwareEntity that = (EquipmentSoftwareEntity) o;
        return Objects.equals(equipment, that.equipment) &&
                Objects.equals(software, that.software);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipment, software);
    }

}