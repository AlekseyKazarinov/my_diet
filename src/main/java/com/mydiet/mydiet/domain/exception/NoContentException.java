package com.mydiet.mydiet.domain.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoContentException extends RuntimeException {

    public NoContentException(String message) {
        super(message);
    }
}
