package com.washup.app.internal.admin.washup_employees.authentication;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.configuration.SecurityConstants.TOKEN_PREFIX;

import com.google.protobuf.Message;
import com.washup.app.spring.EnumerationConverter;
import com.washup.app.spring.ProtobufHttpMessageConverter;
import com.washup.protos.Admin.WashEmployeeLoginRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTWashUpEmployeeAuthenticationFilter
    extends AbstractAuthenticationProcessingFilter {

  private final JWTWashUpEmployeeAuthenticationManager jwtAuthenticationManager;
  private final ProtobufHttpMessageConverter messageConverter;

  public JWTWashUpEmployeeAuthenticationFilter(
      JWTWashUpEmployeeAuthenticationManager jwtAuthenticationManager,
      ProtobufHttpMessageConverter messageConverter) {
    super(new AntPathRequestMatcher("/_admin/login", "POST"));
    this.jwtAuthenticationManager = jwtAuthenticationManager;
    this.messageConverter = messageConverter;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req,
      HttpServletResponse res) throws AuthenticationException {
    if (!req.getServletPath().contains("/_admin/login")) {
      return null;
    }

    HttpInputMessage httpInputMessage = new HttpInputMessage() {
      @Override
      public InputStream getBody() throws IOException {
        return req.getInputStream();
      }

      @Override
      public HttpHeaders getHeaders() {
        Enumeration<String> headerNames = req.getHeaderNames();
        Map<String, List<String>> headers = new LinkedHashMap<>();
        while (headerNames.hasMoreElements()) {
          String headerName = headerNames.nextElement();
          headers.put(headerName, EnumerationConverter.toList(req.getHeaders(headerName)));
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(headers);
        return httpHeaders;
      }
    };

    WashEmployeeLoginRequest.Builder credentials = WashEmployeeLoginRequest.newBuilder();
    try {
      Message message = messageConverter.read(WashEmployeeLoginRequest.class, httpInputMessage);
      credentials.mergeFrom(message);
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