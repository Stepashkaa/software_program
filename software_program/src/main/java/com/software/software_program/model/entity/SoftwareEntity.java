package com.software.software_program.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Check;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "software")
public class SoftwareEntity extends BaseEntity {
    @Check(constraints = "length(name) >= 1")
    @Column(nullable = false, length = 50)
    private String name;

    @Check(constraints = "length(version) >= 1")
    @Column(nullable = false, length = 20)
    private String version;

    @Check(constraints = "length(description) >= 1")
    @Column(name = "description", nullable = false, unique = true, length = 100)
    private String description;

    @OneToMany(mappedBy = "software", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassroomSoftwareEntity> classroomSoftwares = new HashSet<>();

    public SoftwareEntity(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public void addClassroomSoftware(ClassroomSoftwareEntity classroomSoftware) {
        if (classroomSoftware != null) {
            classroomSoftwares.add(classroomSoftware);
            classroomSoftware.setSoftware(this);
        }
    }

    public void removeClassroomSoftware(ClassroomSoftwareEntity classroomSoftware) {
        if (classroomSoftware != null) {
            classroomSoftwares.remove(classroomSoftware);
            classroomSoftware.setSoftware(null);
        }
    }
}
