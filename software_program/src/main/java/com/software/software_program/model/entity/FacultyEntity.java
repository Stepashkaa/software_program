package com.software.software_program.model.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PreRemove;
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
@Table(name = "faculty")
public class FacultyEntity extends BaseEntity {
    @Check(constraints = "length(name) >= 1")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "faculty")
    @OrderBy("id ASC")
    private Set<DepartmentEntity> departments = new HashSet<>();

    public FacultyEntity(String name) {
        this.name = name;
    }

    public void addDepartment(DepartmentEntity department) {
        this.departments.add(department);
        department.setFaculty(this);
    }

    public void removeDepartment(DepartmentEntity department) {
        this.departments.remove(department);
        department.setFaculty(null);
    }

    @PreRemove
    private void preRemove() {
        for (DepartmentEntity department : departments) {
            department.setFaculty(null);
        }
    }
    // FacultyEntity.java
    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        FacultyEntity other = (FacultyEntity) obj;

        return Objects.equals(this.getId(), other.getId())
                && Objects.equals(this.name, other.name);
    }

}
