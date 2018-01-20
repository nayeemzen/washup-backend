package com.washup.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  private String message;

  public BadRequestException() {
    super();
  }

  public BadRequestException(String message) {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

}