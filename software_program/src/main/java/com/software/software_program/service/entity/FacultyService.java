package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.FacultyEntity;
import com.software.software_program.repository.FacultyRepository;
import com.software.software_program.core.eror.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FacultyService extends AbstractEntityService<FacultyEntity> {

    private final FacultyRepository facultyRepository;

    @Transactional(readOnly = true)
    public List<FacultyEntity> getAll(String name) {
        if (name == null || name.isEmpty()) {
            return StreamSupport.stream(facultyRepository.findAll().spliterator(), false).toList();
        }
        return facultyRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Page<FacultyEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isEmpty()) {
            return facultyRepository.findAll(pageable);
        }
        return facultyRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public FacultyEntity get(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(FacultyEntity.class, id));
    }

    @Transactional
    public FacultyEntity create(FacultyEntity entity) {
        validate(entity, true);
        return facultyRepository.save(entity);
    }

    @Transactional
    public FacultyEntity update(long id, FacultyEntity entity) {
        validate(entity, false);
        FacultyEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        syncDepartments(existsEntity, entity.getDepartments());
        return facultyRepository.save(existsEntity);
    }

    @Transactional
    public FacultyEntity delete(long id) {
        FacultyEntity existsEntity = get(id);
        facultyRepository.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<DepartmentEntity> getDepartments(Long facultyId) {
        return get(facultyId).getDepartments().stream().toList();
    }

    @Override
    protected void validate(FacultyEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Faculty entity is null");
        }
        validateStringField(entity.getName(), "Faculty name");
        if (uniqueCheck && facultyRepository.findByNameIgnoreCase(entity.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Faculty with name %s already exists", entity.getName())
            );
        }
    }

    private void syncDepartments(FacultyEntity existsEntity, Set<DepartmentEntity> updatedDepartments) {
        existsEntity.getDepartments().removeIf(department -> !updatedDepartments.contains(department));

        for (DepartmentEntity department : updatedDepartments) {
            if (!existsEntity.getDepartments().contains(department)) {
                existsEntity.addDepartment(department);
            }
        }
    }
}