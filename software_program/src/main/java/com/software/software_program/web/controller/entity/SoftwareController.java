package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.ClassroomDto;
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
    public List<SoftwareDto> getAll() {
        return softwareService.getAll(null).stream()
                .map(softwareMapper::toDto)
                .toList();
    }

    @GetMapping("/search")
    public List<SoftwareDto> searchByName(
            @RequestParam(name = "name") String name
    ) {
        return softwareService.searchByName(name).stream()
                .map(softwareMapper::toDto)
                .toList();
    }

    @GetMapping("/filter")
    public List<SoftwareDto> filterByVersion(
            @RequestParam(name = "version") String version
    ) {
        return softwareService.filterByVersion(version).stream()
                .map(softwareMapper::toDto)
                .toList();
    }


    @GetMapping("/all")
    public List<SoftwareDto> getAll(
            @RequestParam(name = "name", required = false) String name) {
        return softwareService.getAll(name).stream()
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
    @GetMapping("/{id}/equipments")
    public List<Long> getEquipmentsUsingSoftware(@PathVariable Long id) {
        return softwareService.getEquipmentsUsingSoftware(id);
    }
}