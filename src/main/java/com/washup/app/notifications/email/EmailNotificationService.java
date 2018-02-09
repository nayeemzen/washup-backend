package com.washup.app.notifications.email;

import washup.protos.notification.Notification.Email;

public interface EmailNotificationService {
  void sendEmail(Email email);
}
