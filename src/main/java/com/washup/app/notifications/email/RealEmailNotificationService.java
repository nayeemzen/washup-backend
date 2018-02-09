package com.washup.app.notifications.email;

import java.io.IOException;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import washup.protos.notification.Notification.Email;

@Service
public class RealEmailNotificationService implements EmailNotificationService {
  private Logger logger = LoggerFactory.getLogger(RealEmailNotificationService.class);

  @Autowired private SendGridClient sendGridClient;
  @Autowired @Named("notificationTaskExecutor") private TaskExecutor taskExecutor;

  @Override
  public void sendEmail(Email email) {
    taskExecutor.execute(() -> {
      try {
        sendGridClient.send(email);
      } catch (IOException e) {
        logger.error("Failed to send email={}", email, e);
      }
    });
  }
}
