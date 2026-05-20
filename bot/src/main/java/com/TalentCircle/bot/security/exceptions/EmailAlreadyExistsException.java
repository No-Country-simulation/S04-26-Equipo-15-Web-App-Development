package com.TalentCircle.bot.security.exceptions;

public class EmailAlreadyExistsException
        extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
