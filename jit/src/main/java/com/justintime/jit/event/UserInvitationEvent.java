package com.justintime.jit.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserInvitationEvent extends ApplicationEvent {
    private final String toEmail;
    private final String subject;
    private final String body;

    public UserInvitationEvent(Object source, String toEmail, String subject, String body) {
        super(source);
        this.body = body;
        this.subject = subject;
        this.toEmail = toEmail;
    }
}
