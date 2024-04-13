package com.example.cardinalgate.core.enums;

public enum CardType {
    ICODE_SLI(1),
    FELICIA(2);

    private final int value;

    CardType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
