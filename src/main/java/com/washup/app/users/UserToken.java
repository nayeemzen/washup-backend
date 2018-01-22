package com.washup.app.users;

import com.washup.app.tokens.Token;

import static com.google.common.base.Preconditions.checkArgument;

public class UserToken extends Token {

  private static final int LENGTH = 16;
  private static final String PREFIX = "U_";

  public UserToken(String token) {
    super(token);
    checkArgument(token.startsWith(PREFIX), "%s is not UserToken", token);
  }

  public static UserToken of(String token) {
    return new UserToken(token);
  }

  public static UserToken generate() {
    return of(PREFIX + generateToken(LENGTH));
  }
}
