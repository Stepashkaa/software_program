package com.software.software_program.service.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.error.NotFoundException;
import com.software.software_program.core.utility.JwtUtils;
import com.software.software_program.core.utility.ValidationUtils;
import com.software.software_program.model.entity.*;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractEntityService<UserEntity> {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional(readOnly = true)
    public List<UserEntity> getAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> getAll(int page, int size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        return repository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    public UserEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(UserEntity.class, id));
    }

    public UserEntity create(UserEntity entity) {
        validate(entity, null);
        entity.setPhoneNumber(normalizePhoneNumber(entity.getPhoneNumber()));
        if (!entity.getPassword().startsWith("$2a$")) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
        return repository.save(entity);
    }

    @Transactional
    public UserEntity update(long id, UserEntity entity) {
        validate(entity, id);
        final UserEntity existsEntity = get(id);
        existsEntity.setEmail(entity.getEmail());
        existsEntity.setPassword(entity.getPassword());
        existsEntity.setPhoneNumber(entity.getPhoneNumber());
        existsEntity.setRole(entity.getRole());
        existsEntity.setEmailNotificationEnabled(entity.isEmailNotificationEnabled());
        existsEntity.setWebNotificationEnabled(entity.isWebNotificationEnabled());
        syncDepartments(existsEntity, entity.getDepartments());
        syncSoftwareRequests(existsEntity, entity.getSoftwareRequests());
        return repository.save(existsEntity);
    }

    public UserEntity findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    @Transactional
    public UserEntity delete(long id) {
        final UserEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
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

//    @Transactional(readOnly = true)
//    public UserEntity getByEmail(String email) {
//        return repository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
//    }

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

        // Удаление старых заявок
        Set<SoftwareRequestEntity> toRemove = currentRequests.stream()
                .filter(existing -> updatedRequests.stream().noneMatch(updated ->
                        Objects.equals(updated.getId(), existing.getId())))
                .collect(Collectors.toSet());

        for (SoftwareRequestEntity request : toRemove) {
            user.removeSoftwareRequest(request);
        }

        // Добавление новых заявок
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
