package com.example.cardinalgate.core.enums;

import androidx.annotation.NonNull;

import com.example.cardinalgate.R;

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

    public int getHomeImage() {
        switch(this) {
            case IIDX:
                return R.drawable.cab_iidx;
            case SDVX:
                return R.drawable.cab_sdvx;
            case POPN:
                return R.drawable.cab_popn;
            case DDR:
                return R.drawable.cab_ddr;
            case JUBEAT:
                return R.drawable.cab_jubeat;
            case RB:
                return R.drawable.cab_rb;
            case MSC:
                return R.drawable.cab_msc;
            case BS:
                return R.drawable.cab_bs;
            case NST:
                return R.drawable.cab_nst;
            case GD:
                return R.drawable.cab_gd;
            default:
                throw new IllegalArgumentException("Invalid series: " + this);
        }
    }
}
