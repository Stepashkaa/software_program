package com.software.software_program.web.controller.email;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.service.email.EmailService;
import com.software.software_program.web.dto.email.EmailRequestDto;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Controller
@RestController
@RequestMapping(Constants.API_URL + "/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping(value = "/simple", consumes = "application/json")
    public ResponseEntity<String> sendSimpleEmail(
            @RequestBody EmailRequestDto emailRequestDto) {
        emailService.sendSimpleEmail(emailRequestDto);
        return ResponseEntity.ok("Ваше письмо успешно отправлено!");
    }

    @PostMapping(value = "/attachments", consumes = "multipart/form-data")
    public ResponseEntity<String> sendEmailWithAttachments(
            @RequestPart("emailRequest") EmailRequestDto emailRequestDto,
            @RequestPart("attachments") List<MultipartFile> attachments) throws MessagingException {
        emailService.sendEmailWithAttachments(emailRequestDto, attachments);
        return ResponseEntity.ok("Ваше письмо успешно отправлено!");
    }
}
