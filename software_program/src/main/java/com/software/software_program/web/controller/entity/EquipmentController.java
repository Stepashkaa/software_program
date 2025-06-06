package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
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
    @GetMapping
    public PageDto<EquipmentDto> getAll(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "serialNumber", required = false) String serialNumber,
            @RequestParam(name = "classroomId", required = false) Long classroomId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return PageDtoMapper.toDto(
                equipmentService.getAll(type, serialNumber, classroomId, pageable),
                equipmentMapper::toDto
        );
    }
    @GetMapping("/all")
    public List<EquipmentDto> getAllWithoutPagination(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "serialNumber", required = false) String serialNumber,
            @RequestParam(name = "classroomId", required = false) Long classroomId
    ) {
        return equipmentService.getAll(type, serialNumber, classroomId).stream()
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