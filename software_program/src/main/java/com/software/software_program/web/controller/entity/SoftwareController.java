package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.SoftwareDto;
import com.software.software_program.service.entity.SoftwareService;
import com.software.software_program.web.mapper.entity.SoftwareMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/software")
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareService softwareService;
    private final SoftwareMapper softwareMapper;

    @GetMapping
    public Page<SoftwareDto> getAllByFilters(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "version", required = false) String version,
            @RequestParam(name = "description", required = false) String description,
            Pageable pageable
    ) {
        return softwareService.getAllByFilters(name, version, description, pageable)
                .map(softwareMapper::toDto);
    }
    @GetMapping("/all")
    public List<SoftwareDto> getAll(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "version", required = false) String version,
            @RequestParam(name = "description", required = false) String description
    ) {
        return softwareService.getAll(name, version, description).stream()
                .map(softwareMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public SoftwareDto get(@PathVariable Long id) {
        return softwareMapper.toDto(softwareService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SoftwareDto create(@RequestBody @Valid SoftwareDto dto) {
        return softwareMapper.toDto(
                softwareService.create(softwareMapper.toEntity(dto))
        );
    }
    @PutMapping("/{id}")
    public SoftwareDto update(@PathVariable Long id, @RequestBody @Valid SoftwareDto dto) {
        return softwareMapper.toDto(
                softwareService.update(id, softwareMapper.toEntity(dto))
        );
    }
    @DeleteMapping("/{id}")
    public SoftwareDto delete(@PathVariable Long id) {
        return softwareMapper.toDto(softwareService.delete(id));
    }
    @GetMapping("/{id}/classrooms")
    public List<Long> getClassroomsUsingSoftware(@PathVariable Long id) {
        return softwareService.getClassroomsUsingSoftware(id).stream()
                .map(classroomSoftware -> classroomSoftware.getClassroom().getId())
                .toList();
    }
}