package com.software.software_program.service.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.error.NotFoundException;
import com.software.software_program.core.utility.JwtUtils;
import com.software.software_program.core.utility.ValidationUtils;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.repository.UserRepository;
import com.software.software_program.web.dto.authentication.AuthResponseDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractEntityService<UserEntity> {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final NotificationService notificationService;


    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> getAll(String fullName, Pageable pageable) {
        if (fullName == null || fullName.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findByFullNameContainingIgnoreCase(fullName, pageable);
    }

    @Transactional(readOnly = true)
    public UserEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));
    }

    @Transactional
    public UserEntity create(UserEntity entity) {
        validate(entity, null);
        entity.setPhoneNumber(normalizePhoneNumber(entity.getPhoneNumber()));
        if (!entity.getPassword().startsWith("$2a$")) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        UserEntity created = repository.save(entity);

        String msgCreate = String.format(
                "Добавлен новый пользователь: %s (email: %s)",
                created.getFullName(),
                created.getEmail()
        );
        notificationService.sendNotificationToAdmins(msgCreate);

        return created;
    }

    @Transactional
    public UserEntity update(long id, UserEntity dto) {
        validate(dto, id);
        UserEntity exists = get(id);

        exists.setEmail(dto.getEmail());
        exists.setPhoneNumber(dto.getPhoneNumber());
        exists.setRole(dto.getRole());
        exists.setWebNotificationEnabled(dto.isWebNotificationEnabled());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (!dto.getPassword().startsWith("$2a$")) {
                exists.setPassword(passwordEncoder.encode(dto.getPassword()));
            } else {
                exists.setPassword(dto.getPassword());
            }
        }

        syncDepartments(exists, dto.getDepartments());
        syncSoftwareRequests(exists, dto.getSoftwareRequests());

        UserEntity updated = repository.save(exists);

        String msgUpdate = String.format(
                "Обновлён пользователь: %s (email: %s)",
                updated.getFullName(),
                updated.getEmail()
        );
        notificationService.sendNotificationToAdmins(msgUpdate);

        return updated;
    }

    @Transactional
    public UserEntity delete(long id) {
        UserEntity exists = get(id);
        String name = exists.getFullName();
        String email = exists.getEmail();

        for (DepartmentEntity dept : exists.getDepartments()) {
            dept.setHead(null);
        }

        repository.delete(exists);

        String msgDelete = String.format(
                "Удалён пользователь: %s (email: %s)",
                name,
                email
        );
        notificationService.sendNotificationToAdmins(msgDelete);

        return exists;
    }

//    public Long getUserIdFromPrincipal(Principal principal) {
//        UserEntity user = repository.findByEmail(principal.getName())
//                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
//        return user.getId();
//    }


    public UserEntity findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }


    public UserEntity register(UserEntity user) {
        if (repository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public AuthResponseDto login(String email, String rawPassword) {
        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        AuthResponseDto dto = new AuthResponseDto();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return dto;
    }

    public boolean isAdmin(Principal principal) {
        UserEntity user = repository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        // Вот здесь сравниваем enum с enum
        return user.getRole() == UserRole.ADMIN;
    }

    public Long getUserIdFromPrincipal(Principal principal) {
        UserEntity user = repository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        return user.getId();
    }

    @Override
    protected void validate(UserEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("User entity is null");
        }
        validateStringField(String.valueOf(entity.getRole()), "Role user");
        validateStringField(entity.getEmail(), "Email");

        final var existingByEmail = repository.findByEmail(entity.getEmail());
        if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("User with email '%s' already exists", entity.getEmail())
            );
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches(Constants.PHONE_PATTERN)) {
            throw new IllegalArgumentException("Phone number has invalid format: " + phoneNumber);
        }
        String cleaned = phoneNumber.replaceAll("[\\s\\-()]", "");
        if (cleaned.startsWith("8")) {
            cleaned = "+7" + cleaned.substring(1);
        } else if (!cleaned.startsWith("+")) {
            throw new IllegalArgumentException("Phone number must start with +country code or 8");
        }
        return cleaned;
    }

    private void syncDepartments(UserEntity existsEntity, Set<DepartmentEntity> updatedDepartments) {
        Set<DepartmentEntity> departmentsToRemove = existsEntity.getDepartments().stream()
                .filter(department -> !updatedDepartments.contains(department))
                .collect(Collectors.toSet());

        for (DepartmentEntity department : departmentsToRemove) {
            existsEntity.removeDepartment(department);
        }

        for (DepartmentEntity department : updatedDepartments) {
            if (!existsEntity.getDepartments().contains(department)) {
                existsEntity.addDepartment(department);
            }
        }
    }

    private void syncSoftwareRequests(UserEntity user, Set<SoftwareRequestEntity> updatedRequests) {
        Set<SoftwareRequestEntity> currentRequests = new HashSet<>(user.getSoftwareRequests());

        Set<SoftwareRequestEntity> toRemove = currentRequests.stream()
                .filter(existing -> updatedRequests.stream().noneMatch(updated ->
                        Objects.equals(updated.getId(), existing.getId())))
                .collect(Collectors.toSet());

        for (SoftwareRequestEntity request : toRemove) {
            user.removeSoftwareRequest(request);
        }

        for (SoftwareRequestEntity updated : updatedRequests) {
            boolean exists = currentRequests.stream().anyMatch(existing ->
                    Objects.equals(updated.getId(), existing.getId()));
            if (!exists) {
                user.addSoftwareRequest(updated);
            }
        }
    }

    public Optional<UserEntity> getByEmail(String email) {
        return repository.findByEmail(email);
    }


}
