package com.washup.app;

import com.washup.app.integrations.stripe.RealStripeApi;
import com.washup.app.integrations.stripe.StripeApi;
import com.washup.app.notifications.email.EmailNotificationService;
import com.washup.app.notifications.email.FakeEmailNotificationService;
import javax.inject.Singleton;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class WashUpApplication {
  public static void main(String[] args) {
    SpringApplication.run(WashUpApplication.class, args);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public EmailNotificationService emailNotificationService(
      FakeEmailNotificationService fakeEmailNotificationService) {
    return fakeEmailNotificationService;
  }

  @Bean @Singleton
  public StripeApi stripeApi(Environment environment) {
    return new RealStripeApi(environment);
  }
}
