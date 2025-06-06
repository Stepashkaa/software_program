package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.UserDto;
import com.software.software_program.web.dto.pagination.PageDto;
import com.software.software_program.web.mapper.pagination.PageDtoMapper;
import com.software.software_program.web.mapper.entity.UserMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    @GetMapping
    public PageDto<UserDto> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return PageDtoMapper.toDto(userService.getAll(page, size), userMapper::toDto);
    }
    @GetMapping("/{id}")
    public UserDto get(@PathVariable(name = "id") Long id) {
        return userMapper.toDto(userService.get(id));
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