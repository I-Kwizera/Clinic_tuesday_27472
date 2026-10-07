package kigali.clinic.rw.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");

        String rootMsg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();

        if (rootMsg != null && rootMsg.contains("UUID")) {
            error.put("message", "Invalid UUID format. Make sure you replace placeholder text like '<OFFICE_UUID>' with a valid 36-character UUID from your database (or omit the 'office' block entirely if not assigning one yet).");
        } else if (rootMsg != null && (rootMsg.contains("Date") || rootMsg.contains("date"))) {
            error.put("message", "Invalid date format. Expected format is 'yyyy-MM-dd' (for example: \"1985-04-12\").");
        } else {
            error.put("message", "Malformed JSON request body: " + (rootMsg != null ? rootMsg : "Unable to parse JSON."));
        }

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
