package com.example.cardinalgate.core.enums;

public enum CGNotificationChannel {
    RIVAL("rival");

    private final String text;

    CGNotificationChannel(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
