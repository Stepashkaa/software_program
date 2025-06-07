package com.software.software_program.model.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "department")
public class DepartmentEntity extends BaseEntity {
    @Check(constraints = "length(name) >= 1")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private FacultyEntity faculty;

    @OneToMany(mappedBy = "department")
    @OrderBy("id ASC")
    private Set<ClassroomEntity> classrooms = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_id", nullable = false)
    private UserEntity user;

//    @OneToMany(mappedBy = "report")
//    @OrderBy("id ASC")
//    private Set<ReportEntity> reports = new HashSet<>();

    public DepartmentEntity(String name, FacultyEntity faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    public void addClassroom(ClassroomEntity classroom) {
        this.classrooms.add(classroom);
        classroom.setDepartment(this);
    }

    public void removeClassroom(ClassroomEntity classroom) {
        this.classrooms.remove(classroom);
        classroom.setDepartment(null);
    }

//    public void addReport(ReportEntity report) {
//        this.reports.add(report);
//        report.setDepartment(this);
//    }
//
//    public void removeReport(ReportEntity report) {
//        this.reports.remove(report);
//        report.setDepartment(null);
//    }

    @PreRemove
    private void preRemove() {
        for (ClassroomEntity classroom : classrooms) {
            classroom.setDepartment(null);
        }
//        for (ReportEntity report : reports) {
//            report.setDepartment(null);
//        }
    }
}