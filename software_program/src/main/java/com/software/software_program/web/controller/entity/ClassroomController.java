package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.ClassroomDto;
import com.software.software_program.service.entity.ClassroomService;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.web.mapper.entity.ClassroomMapper;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;
    private final ClassroomMapper classroomMapper;

//    @GetMapping
//    public PageDto<ClassroomDto> getAll(
//            @RequestParam(name = "name", required = false) String name,
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size)
//    {
//        return PageDtoMapper.toDto(classroomService.getAll(name, page, size), classroomMapper::toDto);
//    }

    @GetMapping
    public List<ClassroomDto> getAll() {
        return classroomService.getAll(null).stream()
                .map(classroomMapper::toDto)
                .toList();
    }
    @GetMapping("/all")
    public List<ClassroomDto> getAllByName(@RequestParam(name = "name", defaultValue = "") String name) {
        return classroomService.getAll(name).stream()
                .map(classroomMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public ClassroomDto get(@PathVariable Long id) {
        return classroomMapper.toDto(classroomService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassroomDto create(@RequestBody @Valid ClassroomDto dto) {
        return classroomMapper.toDto(classroomService.create(classroomMapper.toEntity(dto)));
    }
    @PutMapping("/{id}")
    public ClassroomDto update(@PathVariable Long id, @RequestBody @Valid ClassroomDto dto) {
        return classroomMapper.toDto(classroomService.update(id, classroomMapper.toEntity(dto)));
    }
    @DeleteMapping("/{id}")
    public ClassroomDto delete(@PathVariable Long id) {
        return classroomMapper.toDto(classroomService.delete(id));
    }
}