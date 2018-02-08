package com.washup.app.spring;

import org.joda.time.DateTime;

public class DateUtils {
  public static Long roundedMillis(DateTime dateTime) {
    long millis = dateTime.getMillis();
    return ((millis + 500) / 1000) * 1000;
  }
}
