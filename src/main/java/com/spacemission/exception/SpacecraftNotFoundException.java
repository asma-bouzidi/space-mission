package com.spacemission.exception;

public class SpacecraftNotFoundException extends RuntimeException {
    public SpacecraftNotFoundException(String message) {
        super(message);
    }
}
