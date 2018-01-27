package com.washup.app.configuration;

import com.washup.app.authentication.JWTAuthenticationFilter;
import com.washup.app.authentication.JWTAuthenticationManager;
import com.washup.app.authentication.JWTAuthorizationFilter;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.users.UserOperator;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.internal.admin.washup_employees.authentication.JWTWashUpEmployeeAuthenticationFilter;
import com.washup.app.internal.admin.washup_employees.authentication.JWTWashUpEmployeeAuthenticationManager;
import com.washup.app.internal.admin.washup_employees.authentication.JWTWashUpEmployeeAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {

  @Autowired
  Transacter transacter;

  @Autowired
  UserOperator.Factory userOperatorFactory;

  @Autowired
  BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    JWTAuthenticationManager authenticationManager =
        new JWTAuthenticationManager(transacter, userOperatorFactory, bCryptPasswordEncoder);
    JWTWashUpEmployeeAuthenticationManager washUpEmployeeAuthManager =
        new JWTWashUpEmployeeAuthenticationManager(transacter, washUpEmployeeOperatorFactory,
            bCryptPasswordEncoder);
    http.cors().and().csrf().disable().authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/v1/users/sign-up").permitAll()
        .anyRequest()
        .authenticated()
        .and()
        // User Authentication Filters
        .addFilterBefore(new JWTAuthenticationFilter(authenticationManager),
            BasicAuthenticationFilter.class)
        .addFilter(new JWTAuthorizationFilter(authenticationManager))
        // Wash Up Employee Authentication Filters
        .addFilterAfter(new JWTWashUpEmployeeAuthenticationFilter(washUpEmployeeAuthManager),
            JWTAuthorizationFilter.class)
        .addFilter(new JWTWashUpEmployeeAuthorizationFilter(washUpEmployeeAuthManager,
            washUpEmployeeOperatorFactory, transacter))
        // this disables session creation on Spring Security
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**",
        new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }
}
