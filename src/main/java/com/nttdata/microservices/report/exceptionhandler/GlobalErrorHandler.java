package com.nttdata.microservices.report.exceptionhandler;

import com.nttdata.microservices.report.exception.AccountException;
import com.nttdata.microservices.report.exception.BadRequestException;
import com.nttdata.microservices.report.exception.ClientException;
import com.nttdata.microservices.report.exception.CreditNotFoundException;
import com.nttdata.microservices.report.exception.DataValidationException;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException ex) {
    log.error("Exception caught in handleRequestBodyError :  {} ", ex.getMessage(), ex);
    var error = ex.getBindingResult().getAllErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .sorted()
        .collect(Collectors.joining(","));
    log.error("errorList : {}", error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(CreditNotFoundException.class)
  public ResponseEntity<String> handleCreditException(CreditNotFoundException ex) {
    log.error("Exception caught in handleCreditException :  {} ", ex.getMessage(), ex);
    log.info("Status value is : {}", ex.getStatusCode());
    return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).body(ex.getMessage());
  }

  @ExceptionHandler(ClientException.class)
  public ResponseEntity<String> handleClientException(ClientException ex) {
    log.error("Exception caught in handleClientException :  {} ", ex.getMessage(), ex);
    log.info("Status value is : {}", ex.getStatusCode());
    return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).body(ex.getMessage());
  }

  @ExceptionHandler(AccountException.class)
  public ResponseEntity<String> handleAccountException(AccountException ex) {
    log.error("Exception caught in handleAccountException :  {} ", ex.getMessage(), ex);
    log.info("Status value is : {}", ex.getStatusCode());
    return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).body(ex.getMessage());
  }

  @ExceptionHandler(DataValidationException.class)
  public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
    log.error("Exception caught in handleDataValidationException :  {} ", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<String> handleClientException(BadRequestException ex) {
    log.error("Exception caught in handleClientException :  {} ", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
    log.info("ConstraintViolationException : {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

}