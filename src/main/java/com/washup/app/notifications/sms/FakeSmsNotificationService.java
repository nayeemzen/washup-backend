package com.washup.app.notifications.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FakeSmsNotificationService implements SmsNotificationService {
  private final static Logger logger = LoggerFactory.getLogger(FakeSmsNotificationService.class);

  @Override
  public void send(String phoneNumber, String text) {
    logger.info("Sms sent [to=%s][message=%s]", phoneNumber, text);
  }
}
