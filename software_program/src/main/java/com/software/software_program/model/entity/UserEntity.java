package com.software.software_program.model.entity;

import com.software.software_program.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "app_users")
public class UserEntity extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String fullName;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true, columnDefinition = "text")
    private String email;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Check(constraints = "length(password) >= 8")
    @Column(nullable = false, length = 60)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean webNotificationEnabled = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NotificationEntity> notifications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SoftwareRequestEntity> softwareRequests = new HashSet<>();

    @OneToMany(mappedBy = "head", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private Set<DepartmentEntity> departments = new HashSet<>();

    public UserEntity(String fullName, String email, String phoneNumber, String password, UserRole role) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public void addNotification(NotificationEntity notification) {
        notifications.add(notification);
        notification.setUser(this);
    }

    public void removeNotification(NotificationEntity notification) {
        notifications.remove(notification);
        notification.setUser(null);
    }

    public void addSoftwareRequest(SoftwareRequestEntity softwareRequest) {
        softwareRequests.add(softwareRequest);
        softwareRequest.setUser(this);
    }

    public void removeSoftwareRequest(SoftwareRequestEntity softwareRequest) {
        softwareRequests.remove(softwareRequest);
        softwareRequest.setUser(null);
    }

    public void addDepartment(DepartmentEntity department) {
        this.departments.add(department);
        department.setHead(this);
    }

    public void removeDepartment(DepartmentEntity department) {
        this.departments.remove(department);
        department.setHead(null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Используем только id
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }

    @PrePersist
    private void setUp() {
        this.createdAt = LocalDateTime.now(ZoneId.of("+00:00"));
    }


}