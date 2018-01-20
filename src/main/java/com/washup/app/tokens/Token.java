package com.washup.app.tokens;

import com.washup.app.database.hibernate.AbstractIdentifier;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class Token extends AbstractIdentifier<String> {

  private static SecureRandom secureRandom = new SecureRandom();
  private static final char[] CHARS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
          .toCharArray();

  protected Token(String token) {
    super(token);
  }

  protected static String generateToken(int length) {
    return generateToken(CHARS, length);
  }

  protected static String generateToken(char[] chars, int length) {
    return RandomStringUtils.random(length, 0, chars.length, false, false,
        chars, secureRandom);
  }
}
