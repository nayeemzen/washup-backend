package com.washup.app.configuration;

import com.google.protobuf.util.JsonFormat;
import com.washup.app.spring.ProtobufHttpMessageConverter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig {
  @Bean
  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufHttpMessageConverter(JsonFormat.printer()
        .includingDefaultValueFields()
        .preservingProtoFieldNames());
  }
}
