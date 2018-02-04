package com.washup.app;

import com.washup.app.AppTester.Factory;
import java.time.Clock;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
@PropertySource("application-development.properties")
public class WashUpTestConfig {

//  @Bean
//  public AppTester.Factory get(@Autowired TestRestTemplate testRestTemplate){
//    return new Factory(testRestTemplate);
//  }

  @Bean
  RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
    return new RestTemplate(Arrays.asList(hmc));
  }

  @Bean
  ProtobufHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufHttpMessageConverter();
  }

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }
}
