package com.washup.app.washup_employees.authentication;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.configuration.SecurityConstants.TOKEN_PREFIX;

import com.google.protobuf.util.JsonFormat;
import com.washup.protos.Internal;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTWashUpEmployeeAuthenticationFilter
    extends AbstractAuthenticationProcessingFilter {

  private final JWTWashUpEmployeeAuthenticationManager jwtAuthenticationManager;

  public JWTWashUpEmployeeAuthenticationFilter(
      JWTWashUpEmployeeAuthenticationManager jwtAuthenticationManager) {
    super(new AntPathRequestMatcher("/_admin/login", "POST"));
    this.jwtAuthenticationManager = jwtAuthenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req,
      HttpServletResponse res) throws AuthenticationException {
    if (!req.getServletPath().contains("/_admin/login")) {
      return null;
    }

    Internal.WashEmployeeLoginRequest.Builder credentials =
        Internal.WashEmployeeLoginRequest.newBuilder();
    try {
      JsonFormat.parser().merge(req.getReader(), credentials);
    } catch (Exception e) {
      throw new BadCredentialsException("invalid email or password");
    }

    WashUpEmployeeAuthenticationToken authentication =
        new WashUpEmployeeAuthenticationToken(credentials.getEmail(), credentials.getPassword());
    return jwtAuthenticationManager.authenticate(authentication);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res, FilterChain chain, Authentication auth)
      throws IOException, ServletException {
    checkState(auth.isAuthenticated());
    checkState(auth instanceof WashUpEmployeeAuthenticationToken);
    WashUpEmployeeAuthenticationToken authenticationToken =
        (WashUpEmployeeAuthenticationToken) auth;
    checkState(authenticationToken.getWashUpEmployeeToken() != null);
    String token = JWTWashUpEmployeeAuthenticationManager.getJwtToken(
        authenticationToken.getWashUpEmployeeToken().getId());
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
  }
}