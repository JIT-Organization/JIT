package com.justintime.jit.event.listeners;

import com.justintime.jit.event.UserInvitationEvent;
import com.justintime.jit.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailEventListener {

    private final EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserInvitedEvent(UserInvitationEvent event) throws MessagingException {
        emailService.send(event.getToEmail(), event.getSubject(), event.getBody(), true);
    }
}
