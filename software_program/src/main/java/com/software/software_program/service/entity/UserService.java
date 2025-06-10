package com.software.software_program.service.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.error.NotFoundException;
import com.software.software_program.core.utility.ValidationUtils;
import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.FacultyEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractEntityService<UserEntity> {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

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
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
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
        syncDepartments(existsEntity, entity.getDepartments());
        return repository.save(existsEntity);
    }

    @Transactional
    public UserEntity delete(long id) {
        final UserEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public UserEntity getByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
    }

    @Override
    protected void validate(UserEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("User entity is null");
        }
        validateStringField(entity.getEmail(), "User email");
        ValidationUtils.validateEmailFormat(entity.getEmail());
        validateStringField(entity.getFullName(), "Full name");
        validateStringField(entity.getPassword(), "User password");
        if (!entity.getPassword().matches(Constants.PASSWORD_PATTERN)) {
            throw new IllegalArgumentException("Password has invalid format: " + entity.getPassword());
        }
        validateStringField(entity.getPhoneNumber(), "User phone number");
        entity.setPhoneNumber(normalizePhoneNumber(entity.getPhoneNumber()));
        if (entity.getRole() == null) {
            throw new IllegalArgumentException("User role must not be null");
        }
        final Optional<UserEntity> existingUser = repository.findByEmail(entity.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("User with email %s already exists", entity.getEmail())
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
        // Находим кафедры для удаления
        Set<DepartmentEntity> departmentsToRemove = existsEntity.getDepartments().stream()
                .filter(department -> !updatedDepartments.contains(department))
                .collect(Collectors.toSet());

        // Удаляем найденные кафедры
        for (DepartmentEntity department : departmentsToRemove) {
            existsEntity.removeDepartment(department);
        }

        // Добавляем новые или обновляем существующие кафедры
        for (DepartmentEntity department : updatedDepartments) {
            if (!existsEntity.getDepartments().contains(department)) {
                existsEntity.addDepartment(department);
            }
        }
    }

}
