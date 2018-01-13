package com.washup.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class WashUpApplication {

	public static void main(String[] args) {
		SpringApplication.run(WashUpApplication.class, args);
	}

	@Bean
	ProtobufHttpMessageConverter httpMessageConverter() {
		return new ProtobufHttpMessageConverter();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
