package com.washup.app.orders;

import com.washup.app.tokens.Token;

import static com.google.common.base.Preconditions.checkArgument;

public class OrderToken extends Token {
  private static final int LENGTH = 8;
  private static final String PREFIX = "#";
  private static final char[] CHARS =
      "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

  public OrderToken(String token) {
    super(token);
    checkArgument(token.startsWith(PREFIX), "%s is not UserToken", token);
  }

  public static OrderToken of(String token) {
    return new OrderToken(token);
  }

  public static OrderToken generate() {
    return of(PREFIX + generateToken(CHARS, LENGTH));
  }
}
