package com.washup.app.notifications.sms;

import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

@Singleton
public class SmsSender {
  private final static Logger logger = LoggerFactory.getLogger(SmsSender.class);

  @Autowired SmsNotificationService smsNotificationService;
  @Autowired @Named("smsNotificationTaskExecutor") private TaskExecutor taskExecutor;

  public void sendSms(String phoneNumber, String text) {
    taskExecutor.execute(() -> {
      try {
        smsNotificationService.send(phoneNumber, text);
      } catch (Exception e) {
        logger.error("Failed to send text", e);
      }
    });
  }

}
