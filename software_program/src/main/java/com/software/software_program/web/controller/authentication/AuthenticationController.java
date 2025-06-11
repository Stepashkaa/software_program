package com.software.software_program.web.controller.authentication;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.core.utility.JwtUtils;
//import com.software.software_program.service.authentication.AuthenticationService;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.authentication.*;
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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
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

