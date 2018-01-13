package com.washup.app.tokens;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class Token {
  private static SecureRandom secureRandom = new SecureRandom();
  private static final char[] CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
          .toCharArray();

  protected String token;

  protected Token(String token) {
    this.token = token;
  }

  public String rawToken() {
    return token;
  }

  protected static String generateToken(int length) {
    return RandomStringUtils.random(length, 0, length - 1, true, true, CHARS,
        secureRandom);
  }
}
