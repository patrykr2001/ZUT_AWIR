package edu.zut.awir.awir4.web.rest;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class RestExceptionHandler {
    public record ApiError(Instant timestamp, int status, String error, String message, String path,
                           Map<String, String> fieldErrors) {
    }

    @ExceptionHandler(UserRestController.NotFoundException.class)
    public ResponseEntity<ApiError>
    handleNotFound(UserRestController.NotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError>
    handleInvalid(MethodArgumentNotValidException ex, WebRequest req) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError>
    handleInvalid2(ConstraintViolationException ex, WebRequest req) {
        Map<String, String> fields = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> fields.put(cv.getPropertyPath().toString(), cv.getMessage()));
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, fields);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, WebRequest req, Map<String, String> fieldErrors) {
        var path = Optional.ofNullable(req.getDescription(false)).orElse("");
        var body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}
