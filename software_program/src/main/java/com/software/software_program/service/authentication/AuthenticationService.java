//package com.software.software_program.service.authentication;
//
//import com.google.common.cache.LoadingCache;
//import com.software.software_program.core.configuration.Constants;
//import com.software.software_program.core.error.InvalidOtpException;
//import com.software.software_program.core.utility.JwtUtils;
//import com.software.software_program.model.entity.UserEntity;
//import com.software.software_program.repository.UserRepository;
//import com.software.software_program.service.email.EmailService;
//import com.software.software_program.web.dto.authentication.LoginRequestDto;
//import com.software.software_program.web.dto.authentication.LoginSuccessDto;
//import com.software.software_program.web.dto.authentication.LogoutRequestDto;
//import com.software.software_program.web.dto.authentication.OtpVerificationRequestDto;
//import com.software.software_program.web.dto.email.EmailRequestDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.CredentialsExpiredException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Random;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final LoadingCache<String, Integer> oneTimePasswordCache;
//    private final EmailService emailService;
//    private final JwtUtils jwtUtils;
//
//    public String login(LoginRequestDto userLoginRequestDto) {
//        final UserEntity user = userRepository.findByEmail(userLoginRequestDto.getEmail())
//                .orElseThrow(() -> new BadCredentialsException("Invalid user email: " + userLoginRequestDto.getEmail()));
//        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("Invalid user password: " + userLoginRequestDto.getPassword());
//        }
//        sendOtp(user, Constants.OTP_EMAIL_SUBJECT);
//        return "Одноразовый пароль успешно отправлен на Вашу электронную почту. Пожалуйста, подтвердите его";
//    }
//
//    public LoginSuccessDto verifyOtp(OtpVerificationRequestDto otpVerificationRequestDto) {
//        UserEntity user = userRepository.findByEmail(otpVerificationRequestDto.getEmail())
//                .orElseThrow(() -> new BadCredentialsException("Invalid user email: " + otpVerificationRequestDto.getEmail()));
//
//        Integer storedOneTimePassword;
//        try {
//            storedOneTimePassword = oneTimePasswordCache.get(user.getEmail());
//        } catch (ExecutionException e) {
//            throw new IllegalStateException("Failed to retrieve OTP from cache due to an internal system error for user " + user.getEmail());
//        }
//
//        if (!storedOneTimePassword.equals(otpVerificationRequestDto.getOneTimePassword())) {
//           throw new InvalidOtpException("Invalid OTP password: " + otpVerificationRequestDto.getOneTimePassword());
//        }
//        user = userRepository.save(user);
//
//        return generateLoginSuccessDto(user);
//    }
//
//    public void invalidateOtp(final String email) {
//        oneTimePasswordCache.invalidate(email);
//    }
//
//    public LoginSuccessDto refreshToken(final String refreshToken) {
//        if (refreshToken == null) {
//            throw new CredentialsExpiredException("Refresh token is missing");
//        }
//        if (jwtUtils.isTokenExpired(refreshToken)) {
//            throw new CredentialsExpiredException("Refresh token has expired");
//        }
//        final var user = userRepository.findByEmail(jwtUtils.extractEmail(refreshToken))
//                .orElseThrow(() -> new BadCredentialsException("Invalid credentials for refresh token"));
//        return generateLoginSuccessDto(user);
//    }
//
//    public void sendOtp(final UserEntity user, final String subject) {
//        oneTimePasswordCache.invalidate(user.getEmail());
//
//        final var otp = new Random().ints(1, 100000, 999999).sum();
//        oneTimePasswordCache.put(user.getEmail(), otp);
//
//        EmailRequestDto emailRequest = new EmailRequestDto();
//        emailRequest.setTo(user.getEmail());
//        emailRequest.setSubject(subject);
//        emailRequest.setMessage("OTP: " + otp+ "\nВремя действия кода ограничено 10 минутами.");
//        CompletableFuture.runAsync(() -> emailService.sendSimpleEmailAsync(emailRequest));
//    }
//
//    private LoginSuccessDto generateLoginSuccessDto(UserEntity user) {
//        if (user == null) {
//            throw new IllegalArgumentException("User cannot be null when generating tokens");
//        }
//        LoginSuccessDto loginSuccessDto = new LoginSuccessDto();
//        loginSuccessDto.setAccessToken(jwtUtils.generateAccessToken(user));
//        loginSuccessDto.setRefreshToken(jwtUtils.generateRefreshToken(user));
//        return loginSuccessDto;
//    }
//}
