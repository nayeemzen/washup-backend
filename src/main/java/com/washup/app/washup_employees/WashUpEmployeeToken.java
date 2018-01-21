package com.washup.app.washup_employees;

import static com.google.common.base.Preconditions.checkArgument;

import com.washup.app.tokens.Token;

public class WashUpEmployeeToken extends Token {

  private static final int LENGTH = 32;
  private static final String PREFIX = "WE_";

  public WashUpEmployeeToken(String token) {
    super(token);
    checkArgument(token.startsWith(PREFIX), "%s is not WashUp Employee token", token);
  }

  public static WashUpEmployeeToken of(String token) {
    return new WashUpEmployeeToken(token);
  }

  public static WashUpEmployeeToken generate() {
    return of(PREFIX + generateToken(LENGTH));
  }
}
