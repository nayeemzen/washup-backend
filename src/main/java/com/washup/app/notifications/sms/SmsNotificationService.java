package com.washup.app.notifications.sms;

public interface SmsNotificationService {
  void send(String phoneNumber, String text);
}
