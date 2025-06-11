package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.DepartmentDto;
import com.software.software_program.web.dto.entity.EquipmentDto;
import com.software.software_program.service.entity.EquipmentService;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.web.mapper.entity.EquipmentMapper;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;
    @GetMapping("/paged")
    public Page<EquipmentDto> getAll(
            @RequestParam(name = "name", required = false) String name,
            Pageable pageable
    ) {
        return equipmentService.getAll(name, pageable)
                .map(equipmentMapper::toDto);
    }
    @GetMapping
    public List<EquipmentDto> getAll(@RequestParam(name = "name", defaultValue = "") String name) {
        return equipmentService.getAll(name).stream()
                .map(equipmentMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public EquipmentDto get(@PathVariable Long id) {
        return equipmentMapper.toDto(equipmentService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentDto create(@RequestBody @Valid EquipmentDto dto) {
        return equipmentMapper.toDto(
                equipmentService.create(equipmentMapper.toEntity(dto))
        );
    }
    @PutMapping("/{id}")
    public EquipmentDto update(@PathVariable Long id, @RequestBody @Valid EquipmentDto dto) {
        return equipmentMapper.toDto(
                equipmentService.update(id, equipmentMapper.toEntity(dto))
        );
    }
    @DeleteMapping("/{id}")
    public EquipmentDto delete(@PathVariable Long id) {
        return equipmentMapper.toDto(equipmentService.delete(id));
    }
}