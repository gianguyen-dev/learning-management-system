package com.group1.MockProject.exception;

import com.group1.MockProject.dto.error.BasicErrorResponseDto;
import com.group1.MockProject.dto.error.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> errors = new HashMap<>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    ErrorResponseDto errorResponseDto =
        new ErrorResponseDto(400, HttpStatus.BAD_REQUEST.getReasonPhrase(), "Lỗi xác thực");
    errorResponseDto.setErrors(errors);

    return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
      errors.put(
          constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    }

    ErrorResponseDto errorResponseDto =
        new ErrorResponseDto(400, HttpStatus.BAD_REQUEST.getReasonPhrase(), "Lỗi xác thực");
    errorResponseDto.setErrors(errors);

    return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<BasicErrorResponseDto> handleIllegalArgumentException(
      IllegalArgumentException exception, WebRequest request) {

    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            400, HttpStatus.BAD_REQUEST.getReasonPhrase(), exception.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<BasicErrorResponseDto> handleBadCredentialsException(
      BadCredentialsException exception, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            401, HttpStatus.UNAUTHORIZED.getReasonPhrase(), exception.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(PaymentRequiredException.class)
  public ResponseEntity<BasicErrorResponseDto> handlePaymentRequiredException(
      PaymentRequiredException exception, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            402, HttpStatus.PAYMENT_REQUIRED.getReasonPhrase(), exception.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.PAYMENT_REQUIRED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<BasicErrorResponseDto> handleAccessDeniedException(
      AccessDeniedException exception, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            403, HttpStatus.FORBIDDEN.getReasonPhrase(), exception.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<BasicErrorResponseDto> handleEmptyResultDataAccessException(
      EmptyResultDataAccessException exception, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            404, HttpStatus.NOT_FOUND.getReasonPhrase(), exception.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(404, HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            405, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), ex.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<BasicErrorResponseDto> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(409, HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UnprocessableEntityException.class)
  public ResponseEntity<BasicErrorResponseDto> handleUnprocessableEntityException(
      UnprocessableEntityException ex, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            422, HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(), ex.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BasicErrorResponseDto> handleAllExceptions(
      Exception ex, WebRequest request) {
    BasicErrorResponseDto responseDto =
        new BasicErrorResponseDto(
            500, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage());
    return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
