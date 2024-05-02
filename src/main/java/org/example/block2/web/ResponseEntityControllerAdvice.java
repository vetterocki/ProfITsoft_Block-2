package org.example.block2.web;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.example.block2.exception.CarManufacturerNotFoundException;
import org.example.block2.web.dto.ExceptionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ResponseEntityControllerAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, List<String>>> handleValidationViolations(
      MethodArgumentNotValidException exception) {
    return exception.getFieldErrors().stream()
        .filter(Objects::nonNull)
        .collect(collectingAndThen(
            groupingBy(FieldError::getField, mapping(FieldError::getDefaultMessage, toList())),
            map -> ResponseEntity.badRequest().body(map))
        );
  }

  private ExceptionResponse buildResponse(String message) {
    return new ExceptionResponse(message,
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
  }

  @ExceptionHandler(CarManufacturerNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleNotFound(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildResponse(exception.getMessage()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ExceptionResponse> handleConstraintViolation(DataIntegrityViolationException exception) {
    String[] splitMessage = exception.getMostSpecificCause().getMessage().split(":\\n?");
    String clientReadable = splitMessage[splitMessage.length - 1].trim();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildResponse(clientReadable));
  }
}
