package com.example.cardinalgate.core.api;

import java.io.IOException;

public class APIUnauthorizedException extends IOException {
    public APIUnauthorizedException(String message) {
        super(message);
    }
}
