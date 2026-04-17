package com.integration.engine.error;

public record EngineError(String type, boolean retriable, String message, Throwable cause) {

    public static EngineError of(String type, boolean retriable, String message, Throwable cause) {
        return new EngineError(type, retriable, message, cause);
    }
}
