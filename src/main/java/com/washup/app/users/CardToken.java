package com.washup.app.users;

import static com.google.common.base.Preconditions.checkArgument;

import com.washup.app.tokens.Token;

public class CardToken extends Token {

  private static final int LENGTH = 16;
  private static final String PREFIX = "CA_";

  public CardToken(String token) {
    super(token);
    checkArgument(token.startsWith(PREFIX), "%s is not CardToken", token);
  }

  public static CardToken of(String token) {
    return new CardToken(token);
  }

  public static CardToken generate() {
    return of(PREFIX + generateToken(LENGTH));
  }
}
