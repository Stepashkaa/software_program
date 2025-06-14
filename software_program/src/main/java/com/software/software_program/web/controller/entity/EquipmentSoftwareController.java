package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.*;
import com.software.software_program.web.dto.entity.EquipmentSoftwareBulkDto;
import com.software.software_program.web.dto.entity.EquipmentSoftwareDto;
import com.software.software_program.service.entity.EquipmentSoftwareService;
import com.software.software_program.web.dto.entity.SoftwareDto;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.web.mapper.entity.EquipmentSoftwareMapper;
import com.software.software_program.web.mapper.entity.SoftwareMapper;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/equipment-software")
@RequiredArgsConstructor
public class EquipmentSoftwareController {

    private final EquipmentSoftwareService equipmentSoftwareService;
    private final EquipmentSoftwareMapper equipmentSoftwareMapper;
    private final SoftwareMapper softwareMapper;

    @GetMapping("/paged")
    public PageDto<EquipmentSoftwareDto> getPagedEquipmentSoftware(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EquipmentSoftwareEntity> entityPage = equipmentSoftwareService.getAll(name, pageable);
        return PageDtoMapper.toDto(entityPage, equipmentSoftwareMapper::toDto);
    }
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
//    @GetMapping("/report/classroom")
//    public List<EquipmentSoftwareDto> getClassroomSoftwareReport(
//            @RequestParam Long classroomId,
//            @RequestParam LocalDate start,
//            @RequestParam LocalDate end
//    ) {
//        return equipmentSoftwareService.getClassroomSoftwareReport(classroomId, start, end).stream()
//                .map(equipmentSoftwareMapper::toDto)
//                .toList();
//    }
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

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBulk(@RequestBody @Valid EquipmentSoftwareBulkDto dto) {
        EquipmentSoftwareDto newDto = new EquipmentSoftwareDto();
        newDto.setEquipmentId(dto.getEquipmentId());
        newDto.setSoftwareIds(dto.getSoftwareIds()); // список
        newDto.setInstallationDate(dto.getInstallationDate());

        equipmentSoftwareService.create(
                equipmentSoftwareMapper.toEntity(newDto)
        );
    }


    @GetMapping("/grouped")
    public List<EquipmentSoftwareDto> getGrouped() {
        List<EquipmentSoftwareEntity> all = equipmentSoftwareService.getAll();

        return all.stream()
                .collect(Collectors.groupingBy(e -> e.getEquipment().getId()))
                .values().stream()
                .map(group -> {
                    EquipmentSoftwareDto dto = new EquipmentSoftwareDto();
                    EquipmentSoftwareEntity first = group.get(0);
                    dto.setEquipmentId(first.getEquipment().getId());
                    dto.setId(first.getId());
                    dto.setEquipmentName(first.getEquipment().getName());
                    dto.setInstallationDate(Formatter.format(first.getInstallationDate()));
                    dto.setSerialNumber(first.getEquipment().getSerialNumber());

                    List<String> softwareNames = group.stream()
                            .flatMap(e -> e.getSoftwares().stream())
                            .map(SoftwareEntity::getName)
                            .distinct()
                            .toList();

                    dto.setSoftwareNames(softwareNames);
                    return dto;
                }).toList();
    }


}