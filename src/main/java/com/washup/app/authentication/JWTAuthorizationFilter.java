package com.washup.app.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.washup.app.api.v1.configuration.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
          HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
            getAuthentication(req);
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
            .setSigningKey(SECRET.getBytes())
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
        return new UsernamePasswordAuthenticationToken(user, null,
            new ArrayList<>());
      }
      return null;
    }
}