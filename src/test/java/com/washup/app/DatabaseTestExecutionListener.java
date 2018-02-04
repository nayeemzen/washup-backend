package com.washup.app;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class DatabaseTestExecutionListener extends AbstractTestExecutionListener {
  private static Logger logger = LoggerFactory.getLogger(DatabaseTestExecutionListener.class);

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    logger.info("Truncating tables");
    //TODO(alihussain): truncate tables instead of cleaning and running migrations again.
    DataSource dataSource = testContext.getApplicationContext()
        .getAutowireCapableBeanFactory()
        .resolveNamedBean(DataSource.class)
        .getBeanInstance();
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.clean();
    flyway.migrate();
    super.afterTestMethod(testContext);
  }
}
