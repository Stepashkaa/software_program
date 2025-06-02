package com.software.software_program.service;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.eror.NotFoundException;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractEntityService<UserEntity> {
    private final UserRepository repository;

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

    @Transactional
    public UserEntity create(UserEntity entity) {
        validate(entity, true);
        return repository.save(entity);
    }

    @Transactional
    public UserEntity update(long id, UserEntity entity) {
        validate(entity, false);
        final UserEntity existsEntity = get(id);
        existsEntity.setEmail(entity.getEmail());
        existsEntity.setPassword(entity.getPassword());
        existsEntity.setPhoneNumber(entity.getPhoneNumber());
        existsEntity.setRole(entity.getRole());
        return repository.save(existsEntity);
    }

    @Transactional
    public UserEntity delete(long id) {
        final UserEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(UserEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("User entity is null");
        }
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
}
