package com.software.software_program.web.controller.authentication;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.utility.JwtUtils;
//import com.software.software_program.service.authentication.AuthenticationService;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.authentication.*;
import com.software.software_program.web.dto.entity.UserDto;
import com.software.software_program.web.mapper.entity.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthRequestDto dto) {
        UserEntity user = new UserEntity();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.ADMIN);

        UserEntity savedUser = userService.register(user);

        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(jwtUtils.generateAccessToken(savedUser));
        response.setRefreshToken(jwtUtils.generateRefreshToken(savedUser));
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(Principal principal) {
        UserEntity user = userService.findByEmail(principal.getName());
        UserDto dto = new UserDto();

        // основные поля
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());

        // флаги уведомлений
        dto.setEmailNotificationEnabled(user.isEmailNotificationEnabled());
        dto.setWebNotificationEnabled(user.isWebNotificationEnabled());

        // уведомления
        dto.setNotificationIds(
                user.getNotifications().stream()
                        .map(NotificationEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setNotificationMessages(
                user.getNotifications().stream()
                        .map(NotificationEntity::getMessage)
                        .collect(Collectors.toList())
        );

        // заявки
        dto.setSoftwareRequestIds(
                user.getSoftwareRequests().stream()
                        .map(SoftwareRequestEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setSoftwareRequestDescriptions(
                user.getSoftwareRequests().stream()
                        .map(SoftwareRequestEntity::getDescription)
                        .collect(Collectors.toList())
        );

        // отделы
        dto.setDepartmentIds(
                user.getDepartments().stream()
                        .map(DepartmentEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setDepartmentNames(
                user.getDepartments().stream()
                        .map(DepartmentEntity::getName)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    /** Получить своё текущее состояние флага */
    @GetMapping("/me/notifications/setting")
    public Map<String, Boolean> getMyWebNotificationSetting(Principal principal) {
        UserEntity me = userService.findByEmail(principal.getName());
        return Map.of("webNotificationEnabled", me.isWebNotificationEnabled());
    }

    /** Переключить своё веб-уведомление */
    @PutMapping("/me/notifications/setting")
    public Map<String, Boolean> updateMyWebNotificationSetting(
            Principal principal,
            @RequestBody Map<String, Boolean> body
    ) {
        UserEntity me = userService.findByEmail(principal.getName());
        boolean enabled = Boolean.TRUE.equals(body.get("webNotificationEnabled"));
        me.setWebNotificationEnabled(enabled);
        UserEntity updated = userService.update(me.getId(), me);
        return Map.of("webNotificationEnabled", updated.isWebNotificationEnabled());
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        AuthResponseDto response = userService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(HttpServletRequest request) {
        String token = jwtUtils.getTokenFromRequest(request);

        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtils.extractEmail(token);
        UserEntity user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        AuthResponseDto dto = new AuthResponseDto();
        dto.setAccessToken(jwtUtils.generateAccessToken(user));
        dto.setRefreshToken(jwtUtils.generateRefreshToken(user));
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());

        return ResponseEntity.ok(dto);
    }

    // ✅ Новый метод для выхода
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}

