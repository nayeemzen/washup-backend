package com.washup.app.api.v1.users;

import com.washup.app.AbstractTest;
import com.washup.app.AppTester;
import com.washup.app.TestUsers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAuthenticationTest extends AbstractTest {
  @Autowired
  AppTester.Factory factory;

  @Test public void signUpTest() throws Exception {
    AppTester markApp = factory.get(TestUsers.MARK);
    markApp.signup();
  }

  @Test public void loginTest() throws Exception {
    AppTester markApp = factory.get(TestUsers.MARK);
    markApp.signup();
    markApp.resetAuthToken();
    markApp.login();
  }
}
