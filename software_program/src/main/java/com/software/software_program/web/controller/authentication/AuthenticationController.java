package com.software.software_program.web.controller.authentication;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.service.authentication.AuthenticationService;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.authentication.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.API_URL)
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<String> userLoginHandler(@RequestBody @Valid final LoginRequestDto userLoginRequestDto) {
        return ResponseEntity.ok(authenticationService.login(userLoginRequestDto));
    }
    @PostMapping(value = "/send-otp")
    public ResponseEntity<Void> sendOtpHandler(@RequestBody @Valid final OtpRequestDto otpRequestDto) {
        authenticationService.sendOtp(userService.getByEmail(otpRequestDto.getEmail()), Constants.OTP_EMAIL_SUBJECT);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/invalidate-otp")
    public ResponseEntity<Void> invalidateOtp(@RequestBody @Valid final OtpRequestDto otpRequestDto) {
        authenticationService.invalidateOtp(otpRequestDto.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/verify-otp")
    public ResponseEntity<LoginSuccessDto> otpVerificationHandler(@RequestBody @Valid final OtpVerificationRequestDto otpVerificationRequestDto,
                                                  HttpServletResponse response) {
        LoginSuccessDto loginSuccessDto = authenticationService.verifyOtp(otpVerificationRequestDto);

        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(loginSuccessDto.getRefreshToken());
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        loginSuccessDto.setRefreshToken(null);
        return ResponseEntity.ok(loginSuccessDto);
    }

    @PostMapping(value = "/verify-otp-direct")
    public LoginSuccessDto otpVerificationDirectHandler(@RequestBody @Valid final OtpVerificationRequestDto otpVerificationRequestDto) {
        return authenticationService.verifyOtp(otpVerificationRequestDto);
    }

    @PutMapping(value = "/refresh-token")
    public ResponseEntity<LoginSuccessDto> tokenRefresherHandler(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                 HttpServletResponse response) {
        LoginSuccessDto loginSuccessDto = authenticationService.refreshToken(refreshToken);

        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(loginSuccessDto.getRefreshToken());
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        loginSuccessDto.setRefreshToken(null);
        return ResponseEntity.ok(loginSuccessDto);
    }

    @PutMapping(value = "/refresh-token-direct")
    public LoginSuccessDto tokenRefresherDirectHandler(@RequestBody(required = false) RefreshTokenRequestDto refreshTokenRequestDto) {
        return authenticationService.refreshToken(refreshTokenRequestDto.getRefreshToken());
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logoutHandler(HttpServletResponse response) {
        ResponseCookie deleteRefreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/refresh-token")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteRefreshTokenCookie.toString());

        return ResponseEntity.noContent().build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/refresh-token")
                .maxAge(30 * 24 * 60 * 60)
                .build();
    }
}
