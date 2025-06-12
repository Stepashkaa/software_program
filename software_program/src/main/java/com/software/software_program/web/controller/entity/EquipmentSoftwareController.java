package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.EquipmentSoftwareDto;
import com.software.software_program.service.entity.EquipmentSoftwareService;
import com.software.software_program.web.dto.entity.SoftwareDto;
import com.software.software_program.web.mapper.entity.EquipmentSoftwareMapper;
import com.software.software_program.web.mapper.entity.SoftwareMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/classroom-software")
@RequiredArgsConstructor
public class EquipmentSoftwareController {

    private final EquipmentSoftwareService equipmentSoftwareService;
    private final EquipmentSoftwareMapper equipmentSoftwareMapper;
    private final SoftwareMapper softwareMapper;
    @GetMapping
    public List<EquipmentSoftwareDto> getAll() {
        return equipmentSoftwareService.getAll().stream()
                .map(equipmentSoftwareMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public EquipmentSoftwareDto get(@PathVariable Long id) {
        return equipmentSoftwareMapper.toDto(equipmentSoftwareService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentSoftwareDto create(@RequestBody @Valid EquipmentSoftwareDto dto) {
        return equipmentSoftwareMapper.toDto(
                equipmentSoftwareService.create(equipmentSoftwareMapper.toEntity(dto))
        );
    }
    @PutMapping("/{id}")
    public EquipmentSoftwareDto update(@PathVariable Long id, @RequestBody @Valid EquipmentSoftwareDto dto) {
        return equipmentSoftwareMapper.toDto(
                equipmentSoftwareService.update(id, equipmentSoftwareMapper.toEntity(dto))
        );
    }
    @DeleteMapping("/{id}")
    public EquipmentSoftwareDto delete(@PathVariable Long id) {
        return equipmentSoftwareMapper.toDto(equipmentSoftwareService.delete(id));
    }
    @GetMapping("/report/classroom")
    public List<EquipmentSoftwareDto> getClassroomSoftwareReport(
            @RequestParam Long classroomId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return equipmentSoftwareService.getClassroomSoftwareReport(classroomId, start, end).stream()
                .map(equipmentSoftwareMapper::toDto)
                .toList();
    }
    @GetMapping("/report/department")
    public List<EquipmentSoftwareDto> getDepartmentSoftwareReport(
            @RequestParam Long departmentId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return equipmentSoftwareService.getDepartmentSoftwareReport(departmentId, start, end).stream()
                .map(equipmentSoftwareMapper::toDto)
                .toList();
    }
    @GetMapping("/unique-software")
    public List<SoftwareDto> getUniqueSoftwareByDepartmentAndPeriod(
            @RequestParam Long departmentId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {
        return equipmentSoftwareService.getUniqueSoftwareByDepartmentAndPeriod(departmentId, start, end).stream()
                .map(softwareMapper::toDto)
                .toList();
    }
}