package com.washup.app;

import static com.google.common.base.Preconditions.checkNotNull;

class TestUser {
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String phoneNumber;

  private TestUser(Builder builder) {
    this.firstName = checkNotNull(builder.firstName);
    this.lastName = checkNotNull(builder.lastName);
    this.email = checkNotNull(builder.email);
    this.phoneNumber = checkNotNull(builder.phoneNumber);
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public static class Builder {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public Builder setFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder setLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public TestUser build() {
      return new TestUser(this);
    }
  }
}