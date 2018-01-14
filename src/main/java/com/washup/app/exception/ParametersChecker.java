package com.washup.app.exception;

public class ParametersChecker {
  public static void check(boolean assertion, String message, String ... args) {
    if (!assertion) {
      throw new BadRequestException(String.format(message, args));
    }
  }
}
