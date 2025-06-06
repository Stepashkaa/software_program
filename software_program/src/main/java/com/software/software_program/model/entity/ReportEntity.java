//package com.software.software_program.model.entity;
//
//import java.time.LocalDate;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//
//import org.hibernate.annotations.Check;
//
//import lombok.Getter;
//import lombok.Setter;
//import lombok.NoArgsConstructor;
//import lombok.EqualsAndHashCode;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@EqualsAndHashCode(callSuper = true)
//@Entity
//@Table(name = "report")
//public class ReportEntity extends BaseEntity {
//    @Column(nullable = false)
//    private LocalDate generatedDate;
//
//    @Column(nullable = false)
//    private LocalDate periodStart;
//
//    @Column(nullable = false)
//    private LocalDate periodEnd;
//
//    @Column(nullable = false, length = 500)
//    private String description;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "department_id", nullable = false)
//    private DepartmentEntity department;
//
//    public ReportEntity(LocalDate generatedDate, LocalDate periodStart, LocalDate periodEnd, String description, DepartmentEntity department) {
//        this.generatedDate = generatedDate;
//        this.periodStart = periodStart;
//        this.periodEnd = periodEnd;
//        this.description = description;
//        this.department = department;
//    }
//}