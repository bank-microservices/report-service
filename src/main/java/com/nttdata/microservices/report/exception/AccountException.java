package com.nttdata.microservices.report.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountException extends RuntimeException {

  private String message;
  private Integer statusCode;

  public AccountException(String message, Integer statusCode) {
    super(message);
    this.message = message;
    this.statusCode = statusCode;
  }

  public AccountException(String message) {
    super(message);
  }

  public AccountException(String message, Throwable cause) {
    super(message, cause);
  }
}
