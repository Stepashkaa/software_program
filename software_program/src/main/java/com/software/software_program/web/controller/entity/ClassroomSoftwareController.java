package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.ClassroomSoftwareDto;
import com.software.software_program.service.entity.ClassroomSoftwareService;
import com.software.software_program.web.mapper.entity.ClassroomSoftwareMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/classroom-software")
@RequiredArgsConstructor
public class ClassroomSoftwareController {

    private final ClassroomSoftwareService classroomSoftwareService;
    private final ClassroomSoftwareMapper classroomSoftwareMapper;
    @GetMapping
    public List<ClassroomSoftwareDto> getAll() {
        return classroomSoftwareService.getAll().stream()
                .map(classroomSoftwareMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public ClassroomSoftwareDto get(@PathVariable Long id) {
        return classroomSoftwareMapper.toDto(classroomSoftwareService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassroomSoftwareDto create(@RequestBody @Valid ClassroomSoftwareDto dto) {
        return classroomSoftwareMapper.toDto(
                classroomSoftwareService.create(classroomSoftwareMapper.toEntity(dto))
        );
    }
    @PutMapping("/{id}")
    public ClassroomSoftwareDto update(@PathVariable Long id, @RequestBody @Valid ClassroomSoftwareDto dto) {
        return classroomSoftwareMapper.toDto(
                classroomSoftwareService.update(id, classroomSoftwareMapper.toEntity(dto))
        );
    }
    @DeleteMapping("/{id}")
    public ClassroomSoftwareDto delete(@PathVariable Long id) {
        return classroomSoftwareMapper.toDto(classroomSoftwareService.delete(id));
    }
    @GetMapping("/report/classroom")
    public List<ClassroomSoftwareDto> getClassroomSoftwareReport(
            @RequestParam Long classroomId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return classroomSoftwareService.getClassroomSoftwareReport(classroomId, start, end).stream()
                .map(classroomSoftwareMapper::toDto)
                .toList();
    }
    @GetMapping("/report/department")
    public List<ClassroomSoftwareDto> getDepartmentSoftwareReport(
            @RequestParam Long departmentId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return classroomSoftwareService.getDepartmentSoftwareReport(departmentId, start, end).stream()
                .map(classroomSoftwareMapper::toDto)
                .toList();
    }
    @GetMapping("/unique-software")
    public List<ClassroomSoftwareDto> getUniqueSoftwareByDepartmentAndPeriod(
            @RequestParam Long departmentId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return classroomSoftwareService.getUniqueSoftwareByDepartmentAndPeriod(departmentId, start, end).stream()
                .map(classroomSoftwareMapper::toDto)
                .toList();
    }
}