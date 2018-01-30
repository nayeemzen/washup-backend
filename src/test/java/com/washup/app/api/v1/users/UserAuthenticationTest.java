package com.washup.app.api.v1.users;

import com.washup.app.AppTester;
import com.washup.app.WashUpTestProviders;
import com.washup.app.TestUsers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WashUpTestProviders.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserAuthenticationTest {
  @Autowired
  AppTester.Factory factory;

  @Test public void basic() throws Exception {
    AppTester markApp = factory.get(TestUsers.MARK);
    markApp.signup();
  }
}
