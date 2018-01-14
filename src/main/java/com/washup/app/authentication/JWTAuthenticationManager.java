package com.washup.app.authentication;

import com.google.common.collect.ImmutableList;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.users.UserOperator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

import static com.washup.app.configuration.SecurityConstants.EXPIRATION_TIME;
import static com.washup.app.configuration.SecurityConstants.SECRET;

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

  public static String getJwtToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, SECRET.getBytes())
        .compact();
  }
}
