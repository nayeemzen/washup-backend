package com.washup.app;

public class TestUsers {
  public final static TestUser MARK = new TestUser.Builder()
      .setFirstName("Mark")
      .setLastName("Cuban")
      .setEmail("mark.cuban@gmail.com")
      .setPhoneNumber("4158801111")
      .build();

  public final static TestUser THOMAS = new TestUser.Builder()
      .setFirstName("Thomas")
      .setLastName("Lee")
      .setEmail("thomas.lee@gmail.com")
      .setPhoneNumber("4167812237")
      .build();
}
