package com.washup.app.common;

import org.springframework.stereotype.Component;

@Component
public class Normalizer {
  public String removeSpaces(String text) {
    return text.replace("\\s+", "");
  }
}
