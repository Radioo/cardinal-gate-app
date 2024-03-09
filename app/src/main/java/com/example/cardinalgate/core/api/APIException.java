package com.example.cardinalgate.core.api;

import java.io.IOException;

public class APIException extends IOException {
    public APIException(String message) {
        super(message);
    }
}
