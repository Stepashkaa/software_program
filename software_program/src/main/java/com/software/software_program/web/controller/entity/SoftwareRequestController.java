package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.SoftwareRequestDto;
import com.software.software_program.model.enums.RequestStatus;
import com.software.software_program.service.entity.SoftwareRequestService;
import com.software.software_program.web.mapper.entity.SoftwareRequestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/software-request")
@RequiredArgsConstructor
public class SoftwareRequestController {

    private final SoftwareRequestService softwareRequestService;
    private final SoftwareRequestMapper softwareRequestMapper;
    private final UserService userService;

    @GetMapping
    public Page<SoftwareRequestDto> getAllByFilters(
            @RequestParam(name = "status", required = false) RequestStatus status,
            @RequestParam(name = "userId", required = false) Long userIdParam,
            Pageable pageable,
            Principal principal
    ) {
        Long currentUserId = userService.getUserIdFromPrincipal(principal);

        // если не админ — непрерывно перезаписываем фильтр, чтобы отдавать только свои заявки
        Long userIdToFilter = userService.isAdmin(principal)
                ? userIdParam    // админ может смотреть чужие (или все, если null)
                : currentUserId; // преподаватель — только свои

        return softwareRequestService
                .getAllByFilters(status, userIdToFilter, pageable)
                .map(softwareRequestMapper::toDto);
    }

    // --- Без пагинации (для экспорта/прочего) ---
    @GetMapping("/all")
    public List<SoftwareRequestDto> getAll(
            @RequestParam(name = "status", required = false) RequestStatus status,
            @RequestParam(name = "userId", required = false) Long userIdParam,
            Principal principal
    ) {
        Long currentUserId = userService.getUserIdFromPrincipal(principal);
        Long userIdToFilter = userService.isAdmin(principal)
                ? userIdParam
                : currentUserId;

        return softwareRequestService.getAll(status, userIdToFilter).stream()
                .map(softwareRequestMapper::toDto)
                .toList();
    }
    @GetMapping("/{id}")
    public SoftwareRequestDto get(@PathVariable Long id) {
        return softwareRequestMapper.toDto(softwareRequestService.get(id));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SoftwareRequestDto create(@RequestBody @Valid SoftwareRequestDto dto, Principal principal) {
        Long currentUserId = userService.getUserIdFromPrincipal(principal);
        dto.setUserId(currentUserId);
        dto.setStatus(RequestStatus.PENDING);

        var entity = softwareRequestMapper.toEntity(dto);
        return softwareRequestMapper.toDto(softwareRequestService.create(entity));
    }

    @GetMapping("/statuses")
    public List<String> getStatuses() {
        return Arrays.stream(RequestStatus.values())
                .map(Enum::name)
                .toList();
    }

    @PutMapping("/{id}")
    public SoftwareRequestDto update(
            @PathVariable Long id,
            @RequestBody SoftwareRequestDto dto
    ) {
        return softwareRequestMapper.toDto(
                softwareRequestService.update(id, dto.getStatus(), dto.getDescription())
        );
    }

    @DeleteMapping("/{id}")
    public SoftwareRequestDto delete(@PathVariable Long id) {
        return softwareRequestMapper.toDto(softwareRequestService.delete(id));
    }
}
