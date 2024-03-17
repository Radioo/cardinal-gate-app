package com.example.cardinalgate.core.enums;

public enum IIDXPlayStyle {
    SINGLE(0),
    DOUBLE(1);

    private final int value;

    IIDXPlayStyle(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static IIDXPlayStyle fromValue(final int value) {
        for (final IIDXPlayStyle style : IIDXPlayStyle.values()) {
            if (style.value == value) {
                return style;
            }
        }

        throw new IllegalArgumentException("Invalid IIDXPlayStyle value: " + value);
    }
}
