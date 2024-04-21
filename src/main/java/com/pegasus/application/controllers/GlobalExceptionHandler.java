package com.pegasus.application.controllers;

import com.pegasus.application.dto.ExceptionDto;
import com.pegasus.application.exeptions.AppException;
import com.pegasus.application.exeptions.UserException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> handleUserException(UserException ex) {
        return wrapIntoResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationErrors(
            ConstraintViolationException exception2
    ) {
       /* List<String> errors = new ArrayList<>();
        Set<ConstraintViolation<?>> fieldErrors = exception2.getConstraintViolations();
        for (ConstraintViolation<?> fieldError: fieldErrors) {
            System.out.println(fieldError);
            errors.add(fieldError.getMessage());
        } */
        return ResponseEntity
                .status(400)
                .body(exception2.getMessage());
    }

    private ResponseEntity<ExceptionDto> wrapIntoResponseEntity (AppException ex, HttpStatus status){
        log.error(ex.getMessage(),ex);
        return ResponseEntity
                .status(status)
                .body(new ExceptionDto(ex));
    }

    private ResponseEntity<ExceptionDto> wrapIntoResponseEntity (ExceptionDto exDto, HttpStatus status){
        return ResponseEntity
                .status(status)
                .body(exDto);
    }
}
