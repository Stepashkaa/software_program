package com.software.software_program.model.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.Check;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "department")
public class DepartmentEntity extends BaseEntity {
    @Check(constraints = "length(name) >= 1")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private FacultyEntity faculty;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<ClassroomEntity> classrooms = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", nullable = false)
    private UserEntity head;
    public DepartmentEntity(String name, FacultyEntity faculty, UserEntity head) {
        this.name = name;
        this.faculty = faculty;
        this.head = head;
    }

    public void addClassroom(ClassroomEntity classroom) {
        this.classrooms.add(classroom);
        classroom.setDepartment(this);
    }

    public void removeClassroom(ClassroomEntity classroom) {
        this.classrooms.remove(classroom);
        classroom.setDepartment(null);
    }

    @PreRemove
    private void preRemove() {
        for (ClassroomEntity classroom : new HashSet<>(classrooms)) {
            removeClassroom(classroom);
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DepartmentEntity other = (DepartmentEntity) obj;
        return Objects.equals(this.getId(), other.getId());
    }
}