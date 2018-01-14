package com.washup.app.authentication;

import com.google.protobuf.util.JsonFormat;
import com.washup.protos.App;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.configuration.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationFilter
      extends AbstractAuthenticationProcessingFilter {
    private final JWTAuthenticationManager jwtAuthenticationManager;

    public JWTAuthenticationFilter(
        JWTAuthenticationManager jwtAuthenticationManager) {
      super(new AntPathRequestMatcher("/api/v1/users/login", "POST"));
      this.jwtAuthenticationManager = jwtAuthenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
          HttpServletResponse res) throws AuthenticationException {
      if (!req.getServletPath().contains("/api/v1/users/login")) {
        return null;
      }

      App.LoginRequest.Builder credentials = App.LoginRequest.newBuilder();
      try {
        JsonFormat.parser().merge(req.getReader(), credentials);
      } catch (Exception e){
        throw new BadCredentialsException("invalid email or password");
      }

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(credentials.getEmail(),
              credentials.getPassword());
      return jwtAuthenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
        HttpServletResponse res, FilterChain chain, Authentication auth)
        throws IOException, ServletException {
      checkState(auth.isAuthenticated());
      String token = JWTAuthenticationManager.getJwtToken(
          (String) auth.getPrincipal());
      res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}