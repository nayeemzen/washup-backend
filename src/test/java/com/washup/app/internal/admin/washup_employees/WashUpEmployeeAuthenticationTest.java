package com.washup.app.internal.admin.washup_employees;

import com.washup.app.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WashUpEmployeeAuthenticationTest extends AbstractTest {
  @Autowired
  WashUpEmployeeAppTester.Factory washUpEmployeeAppTester;

  @Test
  public void loginTest() throws Exception {
    WashUpEmployeeAppTester washUpEmployeeAppTester = this.washUpEmployeeAppTester.onlyCreate();
    washUpEmployeeAppTester.login();
  }
}
