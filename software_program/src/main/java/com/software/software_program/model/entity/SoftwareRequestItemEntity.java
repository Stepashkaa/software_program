package com.software.software_program.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "software_request_item")
public class SoftwareRequestItemEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_request_id")
    private SoftwareRequestEntity softwareRequest;

    @Column(name = "equipment_name", nullable = false, length = 50)
    private String equipmentName;

    @Column(name = "equipment_serial_number", length = 50)
    private String serialNumber;

    @Column(name = "software_name", nullable = false, length = 100)
    private String softwareName;

    @Column(name = "software_type", nullable = false, length = 50)
    private String softwareType;

    @Column(name = "software_description", length = 500)
    private String softwareDescription;


    public SoftwareRequestEntity getRequest() {
        return softwareRequest;
    }

    public void setRequest(SoftwareRequestEntity softwareRequest) {
        this.softwareRequest = softwareRequest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SoftwareRequestItemEntity other = (SoftwareRequestItemEntity) obj;
        return Objects.equals(id, other.id);
    }
}
