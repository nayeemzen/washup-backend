package com.washup.app.common;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.CharMatcher;

public class PhoneNumber {
  private static final CharMatcher NUMBER_MATCHER = CharMatcher.anyOf("0123456789");

  private String phoneNumber;

  private PhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public static PhoneNumber parse(String phoneNumber) {
    String s = NUMBER_MATCHER.retainFrom(phoneNumber);
    checkState(s.length() == 10);
    return new PhoneNumber(s);
  }

  public static boolean isValid(String phoneNumber) {
    return NUMBER_MATCHER.retainFrom(phoneNumber).length() == 10;
  }
}
