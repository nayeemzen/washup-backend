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
  private final static String TWILLIO_SID_KEY_NAME = "TWILLIO_SID";
  private final static String TWILLIO_AUTH_KEY_NAME = "TWILLIO_AUTH_TOKEN";

  private final static PhoneNumber WASHUP_NUMBER = new PhoneNumber("+18887012044");

  @Autowired RealSmsNotificationService(Environment environment) {
    String twillioSid = checkNotNull(environment.getProperty(TWILLIO_SID_KEY_NAME));
    String twillioAuthToken = checkNotNull(environment.getProperty(TWILLIO_AUTH_KEY_NAME));
    Twilio.init(twillioSid, twillioAuthToken);
  }

  @Override
  public void send(String phoneNumber, String text) {
    Message.creator(new PhoneNumber("+1" + phoneNumber), WASHUP_NUMBER, text).create();
  }
}
