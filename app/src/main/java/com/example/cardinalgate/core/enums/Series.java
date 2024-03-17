package com.example.cardinalgate.core.enums;

import androidx.annotation.NonNull;

public enum Series {
    IIDX("iidx"),
    SDVX("sdvx"),
    POPN("popn"),
    DDR("ddr"),
    JUBEAT("jubeat"),
    RB("rb"),
    MSC("msc"),
    BS("bs"),
    NST("nst"),
    GD("gd");

    private final String text;

    Series(final String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
