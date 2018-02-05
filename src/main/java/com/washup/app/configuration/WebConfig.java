package com.washup.app.configuration;

import com.google.protobuf.util.JsonFormat;
import com.washup.app.spring.ProtobufHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
  @Bean
  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufHttpMessageConverter(JsonFormat.printer()
        .includingDefaultValueFields()
        .preservingProtoFieldNames());
  }
}
