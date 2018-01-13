package com.washup.app.user;

import com.washup.app.tokens.Token;

import static com.google.common.base.Preconditions.checkArgument;

public class UserToken extends Token {
  private static final int LENGTH = 16;
  private static final String PREFIX = "U_";

  private UserToken(String token) {
    super(token);
  }

  public static UserToken of(String token) {
    checkArgument(token.startsWith(PREFIX), "%s is not UserToken", token);
    return new UserToken(token);
  }

  public static UserToken generate() {
    return of(PREFIX + generateToken(LENGTH));
  }
}
