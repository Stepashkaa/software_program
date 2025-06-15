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
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "classroomSoftwareId", required = false) Long classroomSoftwareId,
            Pageable pageable
    ) {
        return softwareRequestService.getAllByFilters(status, userId, classroomSoftwareId, pageable)
                .map(softwareRequestMapper::toDto);
    }
    @GetMapping("/all")
    public List<SoftwareRequestDto> getAll(
            @RequestParam(name = "status", required = false) RequestStatus status,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        return softwareRequestService.getAll(status, userId).stream()
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
        // Получаем ID текущего пользователя
        Long currentUserId = userService.getUserIdFromPrincipal(principal);

        dto.setUserId(currentUserId);                     // Устанавливаем пользователя
        dto.setStatus(RequestStatus.PENDING);             // Принудительно статус "Ожидает"

        var entity = softwareRequestMapper.toEntity(dto);

        return softwareRequestMapper.toDto(
                softwareRequestService.create(
                        entity.getRequestDate(),
                        entity.getDescription(),
                        entity.getUser(),
                        entity.getEquipment(),
                        entity.getSoftware(),
                        entity.getRequestedSoftwareName()
                )
        );
    }


    @PutMapping("/{id}")
    public SoftwareRequestDto update(
            @PathVariable Long id,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) String description
    ) {
        return softwareRequestMapper.toDto(
                softwareRequestService.update(id, status, description)
        );
    }
    @DeleteMapping("/{id}")
    public SoftwareRequestDto delete(@PathVariable Long id) {
        return softwareRequestMapper.toDto(softwareRequestService.delete(id));
    }
}
