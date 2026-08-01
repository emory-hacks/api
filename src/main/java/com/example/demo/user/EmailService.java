package com.example.demo.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String from;
    private final String username;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.from:}") String from,
            @Value("${spring.mail.username:}") String username) {
        this.mailSender = mailSender;
        this.from = from;
        this.username = username;
    }

    public void sendVerificationCode(String to, String code) {
        String sender = StringUtils.hasText(from) ? from : username;
        if (!StringUtils.hasText(sender) || !sender.contains("@") || sender.startsWith("@")) {
            throw new IllegalStateException(
                    "Invalid spring.mail.from / username. Set a full address like you@gmail.com");
        }
        if (!StringUtils.hasText(to) || !to.contains("@")) {
            throw new IllegalArgumentException("Invalid recipient email: " + to);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to);
        message.setSubject("Your Emory Hacks verification code");
        message.setText("Your verification code is: " + code + "\n\nThis code expires in 5 minutes.");
        mailSender.send(message);
    }
}
