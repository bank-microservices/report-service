package com.nttdata.microservices.report.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreditNotFoundException extends RuntimeException {

  private String message;
  private Integer statusCode;

  public CreditNotFoundException(String message, Integer statusCode) {
    super(message);
    this.message = message;
    this.statusCode = statusCode;
  }

  public CreditNotFoundException(String message) {
    super(message);
  }

  public CreditNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
