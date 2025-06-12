package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.web.dto.entity.DepartmentDto;
import com.software.software_program.service.entity.DepartmentService;
import com.software.software_program.web.mapper.entity.DepartmentMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;
    @GetMapping
    public List<DepartmentDto> getAll(@RequestParam(name = "name", defaultValue = "") String name) {
        return departmentService.getAll(name).stream()
                .map(departmentMapper::toDto)
                .toList();
    }
    @GetMapping("/paged")
    public Page<DepartmentDto> getAllPaged(
            @RequestParam(name = "name", required = false) String name,
            Pageable pageable
    ) {
        return departmentService.getAll(name, pageable)
                .map(departmentMapper::toDto);
    }
    @GetMapping("/{id}")
    public DepartmentDto get(@PathVariable Long id) {
        return departmentMapper.toDto(departmentService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto create(@RequestBody @Valid DepartmentDto dto) {
        return departmentMapper.toDto(
                departmentService.create(departmentMapper.toEntity(dto))
        );
    }
    @PutMapping("/{id}")
    public DepartmentDto update(@PathVariable Long id, @RequestBody @Valid DepartmentDto dto) {
        return departmentMapper.toDto(
                departmentService.update(id, departmentMapper.toEntity(dto))
        );
    }
    @DeleteMapping("/{id}")
    public DepartmentDto delete(@PathVariable(name = "id") Long id) {
        return departmentMapper.toDto(departmentService.delete(id));
    }
    @GetMapping("/{id}/classrooms")
    public List<Long> getClassrooms(@PathVariable Long id) {
        return departmentService.getClassrooms(id).stream()
                .map(classroom -> classroom.getId())
                .toList();
    }
    @GetMapping("/{id}/software-used")
    public List<Long> getSoftwareUsed(
            @PathVariable Long id,
            @RequestParam(name = "months", defaultValue = "6") int months
    ) {
        return departmentService.getSoftwareUsed(id, months).stream()
                .map(SoftwareEntity::getId)
                .toList();
    }
}
