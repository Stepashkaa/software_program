package com.software.software_program.service.email;

import com.software.software_program.web.dto.email.EmailRequestDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(secondaryEmail);
        simpleMailMessage.setTo(resolveRecipient(emailRequestDto.getTo()));
        simpleMailMessage.setSubject(emailRequestDto.getSubject());
        simpleMailMessage.setText(emailRequestDto.getMessage());
        simpleMailMessage.setReplyTo(emailRequestDto.getFrom());
        emailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmailWithAttachments(EmailRequestDto emailRequestDto, List<MultipartFile> attachments)
            throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(secondaryEmail);
        messageHelper.setTo(resolveRecipient(emailRequestDto.getTo()));
        messageHelper.setSubject(emailRequestDto.getSubject());
        messageHelper.setText(emailRequestDto.getMessage());
        messageHelper.setReplyTo(emailRequestDto.getFrom());

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

    private String resolveRecipient(String to) {
        return (to == null || to.isBlank()) ? supportEmail : to;
    }
}
