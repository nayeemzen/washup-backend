package com.washup.app.common;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.CharMatcher;

public class PostalCode {
  private static final CharMatcher ALPHA_NUMERIC =
      CharMatcher.anyOf("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

  private String phoneNumber;

  private PostalCode(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPostalCode() {
    return this.phoneNumber;
  }

  public static PostalCode parse(String postalCode) {
    String s = ALPHA_NUMERIC.retainFrom(postalCode.toUpperCase());
    checkState(s.length() == 6);
    return new PostalCode(s);
  }

  public static boolean isValid(String postalCode) {
    return ALPHA_NUMERIC.retainFrom(postalCode).length() == 6;
  }
}
