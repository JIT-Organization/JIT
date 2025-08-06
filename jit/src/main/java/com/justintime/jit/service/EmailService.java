package com.justintime.jit.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void send(String toEmail, String subject, String body, boolean isHtml) throws MessagingException;
}
