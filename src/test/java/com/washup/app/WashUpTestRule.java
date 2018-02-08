package com.washup.app;

import org.flywaydb.core.Flyway;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class WashUpTestRule implements MethodRule {

  @Override
  public Statement apply(final Statement base, FrameworkMethod method, Object target) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        Flyway flyway = new Flyway();
        flyway.clean();
        try {
          base.evaluate();
        } finally {
          
        }
      }
    };
  }
}
