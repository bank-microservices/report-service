package com.nttdata.microservices.report.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientException extends RuntimeException {

  private String message;
  private Integer statusCode;

  public ClientException(String message, Integer statusCode) {
    super(message);
    this.message = message;
    this.statusCode = statusCode;
  }

  public ClientException(String message) {
    super(message);
  }

  public ClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
