package com.washup.app.spring;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class EnumerationConverter {
  public static <T> List<T> toList(Enumeration<T> enumeration) {
    List<T> result = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      result.add(enumeration.nextElement());
    }
    return result;
  }
}
