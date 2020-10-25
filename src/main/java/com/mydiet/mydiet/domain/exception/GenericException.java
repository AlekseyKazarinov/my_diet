package com.mydiet.mydiet.domain.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GenericException extends RuntimeException {

    public GenericException(String message) {
        super(message);
    }

    public GenericException(String message, Throwable cause) {
        super(message, cause);
    }

}
