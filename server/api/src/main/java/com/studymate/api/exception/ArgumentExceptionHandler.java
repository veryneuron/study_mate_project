package com.studymate.api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ArgumentExceptionHandler {
    @ExceptionHandler(value={IllegalArgumentException.class, JsonProcessingException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}