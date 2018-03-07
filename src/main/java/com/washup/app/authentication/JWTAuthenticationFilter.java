package com.washup.app.authentication;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.configuration.SecurityConstants.TOKEN_PREFIX;
import static com.washup.app.spring.EnumerationConverter.toList;

import com.google.protobuf.Message;
import com.washup.app.spring.ProtobufHttpMessageConverter;
import com.washup.protos.App;
import com.washup.protos.App.LoginRequest;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JWTAuthenticationFilter
    extends AbstractAuthenticationProcessingFilter {

  private final JWTAuthenticationManager jwtAuthenticationManager;
  private final ProtobufHttpMessageConverter messageConverter;

  public JWTAuthenticationFilter(JWTAuthenticationManager jwtAuthenticationManager,
      ProtobufHttpMessageConverter messageConverter) {
    super(new AntPathRequestMatcher("/api/v1/users/login", "POST"));
    this.jwtAuthenticationManager = jwtAuthenticationManager;
    this.messageConverter = messageConverter;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req,
      HttpServletResponse res) throws AuthenticationException {
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
          headers.put(headerName, toList(req.getHeaders(headerName)));
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(headers);
        return httpHeaders;
      }
    };
    App.LoginRequest.Builder credentials = App.LoginRequest.newBuilder();
    try {
      Message message = messageConverter.read(LoginRequest.class, httpInputMessage);
      credentials.mergeFrom(message);
    } catch (Exception e) {
      throw new BadCredentialsException("invalid email or password");
    }

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(credentials.getEmail(), credentials.getPassword());
    return jwtAuthenticationManager.authenticate(authentication);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req,
      HttpServletResponse res, FilterChain chain, Authentication auth)
      throws IOException, ServletException {
    checkState(auth.isAuthenticated());
    String token = JWTAuthenticationManager.getJwtToken((String) auth.getPrincipal());
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
  }
}