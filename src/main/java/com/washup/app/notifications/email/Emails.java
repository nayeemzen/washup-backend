package com.washup.app.notifications.email;

import com.google.common.collect.ImmutableMap;
import com.washup.protos.App.User;
import washup.protos.notification.Notification.Email;

public class Emails {
  public static Email welcomeEmail(User user) {
    return Email.newBuilder()
        .setTo(user.getEmail())
        .setSubject(EmailTemplate.WELCOME.subject())
        .setTemplateId(EmailTemplate.WELCOME.templateId())
        .putAllTemplateParameters(ImmutableMap.of("firstName", user.getFirstName()))
        .build();
  }
}
