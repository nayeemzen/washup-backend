package com.washup.app;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WashUpCommandLine implements CommandLineRunner {
  private final static Logger logger = LoggerFactory.getLogger(WashUpCommandLine.class);

  private final static String REGISTER_EMPLOYEE_CMD = "register_employee";
  private final static Options options = getOptins();

  @Autowired
  Transacter transacter;

  @Autowired
  WashUpEmployeeOperator.Factory washupEmployeeOperator;

  @Override
  public void run(String... args) throws Exception {
    if (args.length == 0) {
      return;
    }

    DefaultParser defaultParser = new DefaultParser();
    CommandLine cmdLine = defaultParser.parse(options, args);
    if (cmdLine.hasOption(REGISTER_EMPLOYEE_CMD)) {
      String[] optionValues = cmdLine.getOptionValues(REGISTER_EMPLOYEE_CMD);
      Properties optionProperties = getProperties(optionValues);
      String firstName = optionProperties.getProperty("first_name");
      String lastName = optionProperties.getProperty("last_name");
      String email = optionProperties.getProperty("email");
      String password = optionProperties.getProperty("password");

      checkArgument(!Strings.isNullOrEmpty(firstName));
      checkArgument(!Strings.isNullOrEmpty(lastName));
      checkArgument(!Strings.isNullOrEmpty(email));
      checkArgument(!Strings.isNullOrEmpty(password));

      transacter.execute(session -> {
        washupEmployeeOperator.create(session, firstName, lastName, email, password);
      });
      logger.info("Created employee [firstName={}][lastName={}][email={}]", firstName, lastName,
          email);

      // Quit after running the command line, if there was no command line, continue running the app
      System.exit(0);
    }
  }

  private static Properties getProperties(String[] optionValues) {
    checkArgument(optionValues.length % 2 == 0);
    Properties properties = new Properties();
    for (int i = 0; i < optionValues.length; i += 2) {
      properties.put(optionValues[i], optionValues[i+1]);
    }
    return properties;
  }

  private static Options getOptins() {
    Option option = Option.builder("register_employee")
        .hasArgs()
        .numberOfArgs(8)
        .argName("property=value")
        .valueSeparator()
        .build();
    Options options = new Options();
    options.addOption(option);
    return options;
  }
}
