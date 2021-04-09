package com.atlantis.analytics.exception;

public class MissingConfigException extends RuntimeException {
    private static final long serialVersionUID = 6973268139148310086L;
    private final String message;

    public MissingConfigException() {
        super();
        this.message = "Invalid call";
    }

    public MissingConfigException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}