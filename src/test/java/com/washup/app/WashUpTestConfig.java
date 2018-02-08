package com.washup.app;

import com.google.protobuf.util.JsonFormat;
import com.washup.app.spring.ProtobufHttpMessageConverter;
import java.time.Clock;
import java.util.Arrays;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@PropertySource("application-development.properties")
public class WashUpTestConfig {

  @Bean
  RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
    return new RestTemplate(Arrays.asList(hmc));
  }

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufHttpMessageConverter(JsonFormat.printer()
        .includingDefaultValueFields()
        .preservingProtoFieldNames());
  }
}
