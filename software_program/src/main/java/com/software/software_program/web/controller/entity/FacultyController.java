package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.model.entity.FacultyEntity;
import com.software.software_program.web.dto.entity.FacultyDto;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.service.entity.FacultyService;
import com.software.software_program.web.mapper.entity.FacultyMapper;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final FacultyMapper facultyMapper;
    @GetMapping
    public List<FacultyDto> getAll(@RequestParam(name = "name", defaultValue = "") String name) {
        return facultyService.getAll(name).stream()
                .map(facultyMapper::toDto)
                .toList();
    }

    @GetMapping("/paged")
    public PageDto<FacultyDto> getPagedFaculties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FacultyEntity> entityPage = facultyService.getAll(name, pageable);
        return PageDtoMapper.toDto(entityPage, facultyMapper::toDto);
    }

    @GetMapping("/{id}")
    public FacultyDto get(@PathVariable Long id) {
        return facultyMapper.toDto(facultyService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacultyDto create(@RequestBody @Valid FacultyDto dto) {
        return facultyMapper.toDto(
                facultyService.create(facultyMapper.toEntity(dto))
        );
    }
    @PutMapping("/{id}")
    public FacultyDto update(@PathVariable Long id, @RequestBody @Valid FacultyDto dto) {
        return facultyMapper.toDto(
                facultyService.update(id, facultyMapper.toEntity(dto))
        );
    }
    @DeleteMapping("/{id}")
    public FacultyDto delete(@PathVariable Long id) {
        return facultyMapper.toDto(facultyService.delete(id));
    }
    @GetMapping("/{id}/departments")
    public List<Long> getDepartments(@PathVariable Long id) {
        return facultyService.getDepartments(id).stream()
                .map(department -> department.getId())
                .toList();
    }
}
