package com.washup.app.notifications.email;

import com.google.common.collect.ImmutableMap;
import com.washup.protos.App.Address;
import com.washup.protos.App.Order;
import com.washup.protos.App.User;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import washup.protos.notification.Notification.Email;

public class Emails {
  private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter
      .ofLocalizedDate(FormatStyle.FULL)
      .withLocale(Locale.CANADA);

  public static Email welcomeEmail(User user) {
    return Email.newBuilder()
        .setTo(user.getEmail())
        .setSubject(EmailTemplate.WELCOME.subject())
        .setTemplateId(EmailTemplate.WELCOME.templateId())
        .putAllTemplateParameters(ImmutableMap.of("firstName", user.getFirstName()))
        .build();
  }

  public static Email newOrderEmail(User user, Order order, Address address) {
    String pickUp = String.format("%s between 8-10pm", Instant.ofEpochMilli(order.getPickupDate())
        .atZone(ZoneId.of("Canada/Eastern"))
        .format(dateTimeFormatter));
    return Email.newBuilder()
      .setTo(user.getEmail())
      .setSubject(EmailTemplate.NEW_ORDER.subject())
      .setTemplateId(EmailTemplate.NEW_ORDER.templateId())
      .putAllTemplateParameters(ImmutableMap.of(
          "firstName", user.getFirstName(),
          "orderToken", order.getToken().split("#")[1],
          "pickUpDateTime", pickUp,
          "pickUpLocation", address.getStreetAddress()
      ))
      .build();
  }
}
