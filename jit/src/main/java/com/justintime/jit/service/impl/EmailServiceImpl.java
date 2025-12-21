package com.justintime.jit.service.impl;

import com.justintime.jit.entity.FailedEmail;
import com.justintime.jit.repository.FailedEmailRepository;
import com.justintime.jit.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final FailedEmailRepository failedEmailRepository;

    public EmailServiceImpl(JavaMailSender mailSender, FailedEmailRepository failedEmailRepository) {
        this.mailSender = mailSender;
        this.failedEmailRepository = failedEmailRepository;
    }

    @Override
    @Async
    @Retryable(
            value = MailException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void send(String toEmail, String subject, String body, boolean isHtml) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(body, isHtml);
        mailSender.send(message);
    }

    @Recover
    public void recover(MailException e, String to, String subject, String body) {
        FailedEmail failed = new FailedEmail();
        failed.setToEmail(to);
        failed.setSubject(subject);
        failed.setBody(body);
        failed.setFailedAt(LocalDateTime.now());
        failed.setReason(e.getMessage());
        failedEmailRepository.save(failed);
        System.err.println("EMAIL FAILED PERMANENTLY: " + e.getMessage()); // TODO change this to logging aspect
    }
}
