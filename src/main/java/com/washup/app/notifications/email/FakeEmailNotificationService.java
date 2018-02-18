package com.washup.app.notifications.email;

import org.springframework.stereotype.Service;
import washup.protos.notification.Notification.Email;

@Service
public class FakeEmailNotificationService implements EmailNotificationService {

  @Override
  public void sendEmail(Email email) {
  }
}
