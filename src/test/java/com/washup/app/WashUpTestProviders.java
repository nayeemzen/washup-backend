package com.washup.app;

import com.washup.app.AppTester.Factory;
import com.washup.app.database.hibernate.Transacter;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class WashUpTestProviders {

  @Bean
  public AppTester.Factory get(@Autowired TestRestTemplate testRestTemplate){
    return new Factory(testRestTemplate);
  }

  @Bean
  ProtobufHttpMessageConverter httpMessageConverter() {
    return new ProtobufHttpMessageConverter();
  }

  @Bean
  RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
    return new RestTemplate(Arrays.asList(hmc));
  }
}
