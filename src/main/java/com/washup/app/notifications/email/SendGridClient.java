package com.washup.app.notifications.email;

import static com.google.common.base.Preconditions.checkNotNull;

import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import washup.protos.notification.Notification.Email;

@Service
class SendGridClient {
  private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
  private static final String SENDER_EMAIL = "sender_email_address";
  private final SendGrid sendGrid;
  private final String senderEmail;

  @Autowired
  SendGridClient(Environment environment) {
    this.sendGrid = null;//new SendGrid(checkNotNull(environment.getProperty(SENDGRID_API_KEY)));
    this.senderEmail = null;//checkNotNull(environment.getProperty(SENDER_EMAIL));
  }

  void send(Email email) throws IOException {
    Mail mail = new Mail();
    mail.setTemplateId(email.getTemplateId());
    mail.setFrom(new com.sendgrid.Email(senderEmail));
    mail.setSubject(email.getSubject());
    mail.addContent(new Content("text/plain", "Email from WashUp."));
    mail.addContent(new Content("text/html",  "Email from WashUp."));

    Personalization personalization = new Personalization();
    personalization.addTo(new com.sendgrid.Email(email.getTo()));
    personalization.setSubject(email.getSubject());
    for (Map.Entry<String, String> templateParam: email.getTemplateParametersMap().entrySet()) {
      personalization.addSubstitution(templateParam.getKey(), templateParam.getValue());
    }

    mail.addPersonalization(personalization);

    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());
    sendGrid.api(request);
  }
}
