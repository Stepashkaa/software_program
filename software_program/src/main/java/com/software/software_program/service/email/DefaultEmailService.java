package com.software.software_program.service.email;

import com.software.software_program.web.dto.email.EmailRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultEmailService implements EmailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String secondaryEmail;

    @Value("${support.email}")
    private String supportEmail;

    @Override
    public void sendSimpleEmail(EmailRequestDto emailRequestDto) {
        try {
            SimpleMailMessage message = createSimpleMailMessage(emailRequestDto);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void sendSimpleEmailAsync(EmailRequestDto emailRequestDto) {
        SimpleMailMessage message = createSimpleMailMessage(emailRequestDto);
        emailSender.send(message);
    }

    @Override
    public void sendEmailWithAttachments(EmailRequestDto emailRequestDto, List<MultipartFile> attachments)
            throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setTo(resolveRecipient(emailRequestDto.getTo()));
        messageHelper.setFrom(secondaryEmail);
        messageHelper.setSubject(emailRequestDto.getSubject());
        messageHelper.setText(emailRequestDto.getMessage());
        if (emailRequestDto.getFrom() != null && !emailRequestDto.getFrom().isBlank()) {
            messageHelper.setReplyTo(emailRequestDto.getFrom());
        }

        int counter = 1;
        for (MultipartFile file : attachments) {
            String filename = (file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank())
                    ? file.getOriginalFilename()
                    : "attachment_" + counter;
            messageHelper.addAttachment(filename, file);
            counter++;
        }

        emailSender.send(mimeMessage);
    }
    private SimpleMailMessage createSimpleMailMessage(EmailRequestDto emailRequestDto) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(secondaryEmail);
        simpleMailMessage.setTo(resolveRecipient(emailRequestDto.getTo()));
        simpleMailMessage.setSubject(emailRequestDto.getSubject());
        simpleMailMessage.setText(emailRequestDto.getMessage());
        if (emailRequestDto.getFrom() != null && !emailRequestDto.getFrom().isBlank()) {
            simpleMailMessage.setReplyTo(emailRequestDto.getFrom());
        }
        return simpleMailMessage;
    }


    private String resolveRecipient(String to) {
        return (to == null || to.isBlank()) ? supportEmail : to;
    }
}
