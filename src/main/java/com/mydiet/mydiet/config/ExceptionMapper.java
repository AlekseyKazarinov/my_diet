package com.mydiet.mydiet.config;

import com.mydiet.mydiet.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionMapper extends ResponseEntityExceptionHandler  {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> toResponse(NotFoundException e) {
        var errorMessage = ErrorMessage.builder()
                                       .message(e.getMessage())
                                       .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
