package com.gs.api.service.impl;

import com.gs.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-sender}")
    private String sender;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String message, List<String> emails) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setSubject("Return the Book!");
        mailMessage.setText(message);
        mailMessage.setTo(emails.toArray(new String[0]));
        javaMailSender.send(mailMessage);
    }
}
