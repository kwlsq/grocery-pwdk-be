package com.pwdk.grocereach.Auth.Application.Implements;

import com.pwdk.grocereach.Auth.Application.Services.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verify Your Grocereach Account");

            // Include email parameter in the verification URL
            String verificationUrl = "http://localhost:3000/verify/" + token + "?email=" + URLEncoder.encode(to, StandardCharsets.UTF_8);

            String htmlMsg = "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                    + "<h2 style='color: #059669;'>Welcome to Grocereach!</h2>"
                    + "<p>Thank you for registering. Please click the button below to verify your email address and set your password.</p>"
                    + "<p>This link is valid for 1 hour.</p>"
                    + "<a href='" + verificationUrl + "' "
                    + "style='"
                    + "display: inline-block; "
                    + "padding: 12px 24px; "
                    + "margin: 20px 0; "
                    + "font-size: 16px; "
                    + "font-weight: bold; "
                    + "color: #ffffff; "
                    + "background-color: #10B981; "
                    + "text-decoration: none; "
                    + "border-radius: 5px;"
                    + "'>"
                    + "Verify Account"
                    + "</a>"
                    + "<p>If you did not create an account, no further action is required.</p>"
                    + "</div>"
                    + "</body>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Reset Your Grocereach Password");

            String resetUrl = "http://localhost:3000/reset-password/" + token; // Using clean URL
            String htmlMsg = "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                    + "<div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                    + "<h2 style='color: #059669;'>Password Reset Request</h2>"
                    + "<p>You are receiving this email because a password reset request was made for your account.</p>"
                    + "<p>Please click the button below to set a new password. This link is valid for 1 hour.</p>"
                    + "<a href='" + resetUrl + "' "
                    + "style='"
                    + "display: inline-block; "
                    + "padding: 12px 24px; "
                    + "margin: 20px 0; "
                    + "font-size: 16px; "
                    + "font-weight: bold; "
                    + "color: #ffffff; "
                    + "background-color: #10B981; "
                    + "text-decoration: none; "
                    + "border-radius: 5px;"
                    + "'>"
                    + "Reset Password"
                    + "</a>"
                    + "<p>If you did not request a password reset, please ignore this email.</p>"
                    + "</div>"
                    + "</body>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }}
