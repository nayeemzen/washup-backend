package com.washup.app.notifications.sms;

import static com.google.common.base.Preconditions.checkNotNull;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RealSmsNotificationService implements SmsNotificationService {
  private final static String TWILIO_SID_KEY_NAME = "TWILIO_SID";
  private final static String TWILIO_AUTH_KEY_NAME = "TWILIO_AUTH_TOKEN";

  private final static PhoneNumber WASHUP_NUMBER = new PhoneNumber("+18887012044");

  @Autowired RealSmsNotificationService(Environment environment) {
    String TWILIOSid = checkNotNull(environment.getProperty(TWILIO_SID_KEY_NAME));
    String TWILIOAuthToken = checkNotNull(environment.getProperty(TWILIO_AUTH_KEY_NAME));
    Twilio.init(TWILIOSid, TWILIOAuthToken);
  }

  @Override
  public void send(String phoneNumber, String text) {
    PhoneNumber number = text.startsWith("1")
        ? new PhoneNumber("+" + text)
        : new PhoneNumber("+1" + phoneNumber);
    Message.creator(number, WASHUP_NUMBER, text).create();
  }
}
