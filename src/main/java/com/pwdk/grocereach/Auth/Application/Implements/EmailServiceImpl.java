package com.pwdk.grocereach.Auth.Application.Implements;

import com.pwdk.grocereach.Auth.Application.Services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String subject = "Grocereach Email Verification";
        String verificationLink = "http://localhost:3000/verify?token=" + token;

        String body = "Hello,\n\n"
                + "Please verify your Grocereach account by clicking the link below:\n"
                + verificationLink + "\n\n"
                + "This link will expire in 1 hour.\n\n"
                + "Regards,\n"
                + "Grocereach Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("noreply@grocereach.com");

        mailSender.send(message);
    }
}
