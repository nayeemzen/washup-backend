package com.washup.app.internal.admin.washup_employees.authentication;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.configuration.SecurityConstants.TOKEN_PREFIX;
import static com.washup.app.configuration.SecurityConstants.WASHUP_EMPLOYEE_JWT_SECRET;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTWashUpEmployeeAuthorizationFilter extends BasicAuthenticationFilter {

  private final JWTWashUpEmployeeAuthenticationManager washUpEmployeeAuthenticationManager;
  private final WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;
  private final Transacter transacter;

  public JWTWashUpEmployeeAuthorizationFilter(
      JWTWashUpEmployeeAuthenticationManager washUpEmployeeAuthenticationManager,
      WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory,
      Transacter transacter) {
    super(washUpEmployeeAuthenticationManager);
    this.washUpEmployeeAuthenticationManager = washUpEmployeeAuthenticationManager;
    this.washUpEmployeeOperatorFactory = washUpEmployeeOperatorFactory;
    this.transacter = transacter;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req,
      HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    // Don't authorize as wash up employee if not hitting the _admin path
    if (!req.getServletPath().startsWith("/_admin/")) {
      chain.doFilter(req, res);
      return;
    }
    String header = req.getHeader(HEADER_STRING);
    if (header == null || !header.startsWith(TOKEN_PREFIX)) {
      chain.doFilter(req, res);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(
      HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token == null) {
      return null;
    }

    Jws<Claims> claimsJws;
    try {
      claimsJws = Jwts.parser()
          .setSigningKey(WASHUP_EMPLOYEE_JWT_SECRET.getBytes())
          // parse will throw if their is a signature mismatch
          .parseClaimsJws(token.replace(TOKEN_PREFIX, ""));
    } catch (SignatureException e) {
      // the signature was fidled locally.
      //LOG
      return null;
    }

    String user = claimsJws
        .getBody()
        .getSubject();
    if (user != null) {
      return transacter.call(session -> {
        WashUpEmployeeOperator washUpEmployee = washUpEmployeeOperatorFactory
            .getWashUpEmployeeByToken(session, new WashUpEmployeeToken(user));
        checkState(washUpEmployee != null);
        return new WashUpEmployeeAuthenticationToken(user, washUpEmployee.getToken());
      });
    }
    return null;
  }
}