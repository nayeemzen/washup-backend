package com.washup.app;

import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class WashUpCommandLine implements CommandLineRunner {

  @Autowired
  WashUpEmployeeOperator.Factory washupEmployeeOperator;

  @Override
  public void run(String... args) throws Exception {
    if (args.length == 0) {
      return;
    }

    Option option = Option.builder("register_employee")
        .hasArgs()
        .argName("property=value")
        .valueSeparator()
        .build();
    Options options = new Options();
    options.addOption(option);

    DefaultParser defaultParser = new DefaultParser();
    CommandLine cmdLine = defaultParser.parse(options, args);
    if (cmdLine.hasOption("register_employee")) {
      System.out.println();
    }

    // Exit
    System.exit(0);
  }
}
