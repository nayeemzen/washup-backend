package com.washup.app.internal.admin.washup_employees.authentication;

import com.google.common.collect.ImmutableList;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeToken;
import javax.annotation.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class WashUpEmployeeAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private final @Nullable WashUpEmployeeToken washUpEmployeeToken;

  public WashUpEmployeeAuthenticationToken(Object principal, Object credentials) {
    super(principal, credentials);
    washUpEmployeeToken = null;
  }

  public WashUpEmployeeAuthenticationToken(Object principal,
      WashUpEmployeeToken washUpEmployeeToken) {
    super(principal, null, ImmutableList.of());
    this.washUpEmployeeToken = washUpEmployeeToken;
  }

  public @Nullable WashUpEmployeeToken getWashUpEmployeeToken() {
    return washUpEmployeeToken;
  }
}
