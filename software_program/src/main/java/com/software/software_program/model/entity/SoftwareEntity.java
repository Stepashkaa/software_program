package com.software.software_program.model.entity;

import com.software.software_program.model.enums.TypeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.Check;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "software")
public class SoftwareEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String version;

    @Column(name = "description", nullable = false, unique = true, length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'FREE'")
    private TypeStatus type = TypeStatus.FREE;

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EquipmentSoftwareEntity> equipmentSoftwares = new HashSet<>();

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SoftwareRequestEntity> softwareRequests = new HashSet<>();

    public SoftwareEntity(String name, String version, String description, TypeStatus type) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.type = type;
    }

    public void addEquipmentSoftware(EquipmentSoftwareEntity equipmentSoftware) {
        if (equipmentSoftware != null) {
            equipmentSoftwares.add(equipmentSoftware);
            equipmentSoftware.setSoftware(this);
        }
    }

    public void removeEquipmentSoftware(EquipmentSoftwareEntity equipmentSoftware) {
        if (equipmentSoftware != null) {
            equipmentSoftwares.remove(equipmentSoftware);
            equipmentSoftware.setSoftware(null);
        }
    }

    public void addSoftwareRequest(SoftwareRequestEntity request) {
        if (request != null) {
            softwareRequests.add(request);
            request.setSoftware(this);
        }
    }

    public void removeSoftwareRequest(SoftwareRequestEntity request) {
        if (request != null) {
            softwareRequests.remove(request);
            request.setSoftware(null);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoftwareEntity that = (SoftwareEntity) o;
        return Objects.equals(id, that.id);
    }
}
