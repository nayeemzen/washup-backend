package com.washup.app.washup_employees.authentication;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.configuration.SecurityConstants.EXPIRATION_TIME;
import static com.washup.app.configuration.SecurityConstants.WASHUP_EMPLOYEE_JWT_SECRET;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.washup_employees.WashUpEmployeeOperator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class JWTWashUpEmployeeAuthenticationManager implements AuthenticationManager {

  Transacter transacter;
  WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;
  BCryptPasswordEncoder bCryptPasswordEncoder;

  public JWTWashUpEmployeeAuthenticationManager(Transacter transacter,
      WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory,
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.transacter = transacter;
    this.washUpEmployeeOperatorFactory = washUpEmployeeOperatorFactory;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  public static String getJwtToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, WASHUP_EMPLOYEE_JWT_SECRET.getBytes())
        .compact();
  }

  @Override
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException {
    // Make sure authentication is for wash up employees
    checkState(authentication instanceof WashUpEmployeeAuthenticationToken);
    String email = (String) authentication.getPrincipal();
    String rawPassword = (String) authentication.getCredentials();

    WashUpEmployeeOperator employeeOperator = transacter.call(session ->
        washUpEmployeeOperatorFactory.getWashUpEmployeeByEmail(session, email));
    // if user is not found throw
    if (employeeOperator == null) {
      throw new BadCredentialsException("Email or Password is incorrect!");
    }

    // if password doesn't match, throw
    if (!bCryptPasswordEncoder.matches(rawPassword, employeeOperator.getEncodedPassword())) {
      throw new BadCredentialsException("Email or Password is incorrect!");
    }

    // If username was found and password has matched, authenticate!
    return new WashUpEmployeeAuthenticationToken(authentication.getPrincipal(),
        employeeOperator.getToken());
  }
}
