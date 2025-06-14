package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.ClassroomDto;
import com.software.software_program.web.dto.entity.UserDto;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import com.software.software_program.web.mapper.entity.UserMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/user-roles")
    public List<Map<String, String>> getUserRoles() {
        return Arrays.stream(UserRole.values())
                .map(role -> Map.of(
                        "value", role.name(),
                        "label", switch (role) {
                            case ADMIN -> "Администратор";
                            case TEACHER -> "Преподаватель";
                        }
                ))
                .toList();
    }

    @GetMapping("/paged")
    public PageDto<UserDto> getPagedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) String fullName
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> entityPage = userService.getAll(fullName, pageable);
        return PageDtoMapper.toDto(entityPage, userMapper::toDto);
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable(name = "id") Long id) {
        return userMapper.toDto(userService.get(id));
    }

    @GetMapping("/all")
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto dto) {
        return userMapper.toDto(userService.create(userMapper.toEntity(dto)));
    }
    @PutMapping("/{id}")
    public UserDto update(@PathVariable(name = "id") Long id, @RequestBody @Valid UserDto dto) {
        return userMapper.toDto(userService.update(id, userMapper.toEntity(dto)));
    }
    @DeleteMapping("/{id}")
    public UserDto delete(@PathVariable(name = "id") Long id) {
        return userMapper.toDto(userService.delete(id));
    }
}