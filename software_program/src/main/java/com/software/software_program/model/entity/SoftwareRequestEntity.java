package com.software.software_program.model.entity;

import com.software.software_program.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "software_request")
public class SoftwareRequestEntity extends BaseEntity {

    @Column(nullable = false)
    private Date requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private EquipmentEntity equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "software_id")
    private SoftwareEntity software;

    @Column(name = "requested_software_name", length = 100)
    private String requestedSoftwareName;

    public SoftwareRequestEntity(Date requestDate, RequestStatus status, String description, UserEntity user, EquipmentEntity equipment, SoftwareEntity software, String requestedSoftwareName) {
        this.requestDate = requestDate;
        this.status = status;
        this.description = description;
        this.user = user;
        this.equipment = equipment;
        this.software = software;
        this.requestedSoftwareName = requestedSoftwareName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SoftwareRequestEntity other = (SoftwareRequestEntity) obj;
        return Objects.equals(id, other.id);
    }
}
