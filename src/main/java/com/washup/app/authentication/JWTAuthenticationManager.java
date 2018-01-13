package com.washup.app.authentication;

import com.google.common.collect.ImmutableList;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.users.UserOperator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class JWTAuthenticationManager implements AuthenticationManager {
  Transacter transacter;
  UserOperator.Factory userOperatorFactory;
  BCryptPasswordEncoder bCryptPasswordEncoder;

  public JWTAuthenticationManager(Transacter transacter,
      UserOperator.Factory userOperatorFactory,
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.transacter = transacter;
    this.userOperatorFactory = userOperatorFactory;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
    String email = (String) authentication.getPrincipal();
    String rawPassword = (String) authentication.getCredentials();

    UserOperator user = transacter.call(session ->
      userOperatorFactory.getUserByEmail(session, email));
    // if user is not found throw
    if (user == null) {
      throw new BadCredentialsException("Email or Password is incorrect!");
    }

    // if password doesn't match, throw
    if (!bCryptPasswordEncoder.matches(rawPassword,
          user.getEncodedPassword())) {
      throw new BadCredentialsException("Email or Password is incorrect!");
    }

    // If username was found and password has matched, authenticate!
    return new UsernamePasswordAuthenticationToken(
        authentication.getPrincipal(), authentication.getCredentials(),
        ImmutableList.of());
  }
}
