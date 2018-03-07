package com.washup.app.common;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
  public boolean isValid(String email) {
    return !email.contains("\\s") && email.contains("@");
  }
}
