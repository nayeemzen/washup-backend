package com.washup.app.configuration;

public class SecurityConstants {
  //TODO: MOVE THIS TO SOMETHING SECURE
  public static final String SECRET = "zVttFEeyXPCvhXAjLjAfaLpgV9Nhe2wW";
  public static final long EXPIRATION_TIME = 864_000_000; // 10 days
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
}
