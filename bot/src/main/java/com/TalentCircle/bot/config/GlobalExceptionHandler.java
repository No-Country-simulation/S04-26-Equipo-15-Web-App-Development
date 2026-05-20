package com.TalentCircle.bot.config;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.TalentCircle.bot.ai.exceptions.SummaryGenerationException;
import com.TalentCircle.bot.security.exceptions.EmailAlreadyExistsException;
import com.TalentCircle.bot.security.exceptions.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailExists(
                        EmailAlreadyExistsException ex) {

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(
                                                new ErrorResponse(
                                                                ex.getMessage(),
                                                                HttpStatus.CONFLICT.value(),
                                                                LocalDateTime.now()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex) {

                String message = ex.getBindingResult()
                                .getFieldError()
                                .getDefaultMessage();

                return ResponseEntity
                                .badRequest()
                                .body(
                                                new ErrorResponse(
                                                                message,
                                                                HttpStatus.BAD_REQUEST.value(),
                                                                LocalDateTime.now()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneric(
                        Exception ex) {

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                new ErrorResponse(
                                                                ex.getMessage(),
                                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                                LocalDateTime.now()));
        }

        @ExceptionHandler(SummaryGenerationException.class)
        public ResponseEntity<ErrorResponse> handleSummaryError(
                        SummaryGenerationException ex) {

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                new ErrorResponse(
                                                                ex.getMessage(),
                                                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                                LocalDateTime.now()));
        }
}