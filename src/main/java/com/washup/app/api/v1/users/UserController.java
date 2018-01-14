package com.washup.app.api.v1.users;

import com.google.common.base.Strings;
import com.google.protobuf.util.JsonFormat;
import com.washup.app.authentication.JWTAuthenticationManager;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.washup.app.api.v1.ApiConstants.API_URL;
import static com.washup.app.api.v1.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.api.v1.configuration.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping(UserController.URL)
public class UserController {
  static final String URL = API_URL + "/users";

  @Autowired Transacter transacter;
  @Autowired UserOperator.Factory userOperatorFactory;

  @PostMapping("/sign-up")
  public ResponseEntity<String> signUp(
      @RequestBody App.SignUpRequest request) {
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getFirstName()),
        "first_name is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getLastName()),
        "last_name is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getEmail()),
        "email is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getEmail()),
        "password is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getPhoneNumber()),
        "phone_number is missing");

    ResponseEntity<String> response = transacter.call(session -> {
      UserOperator userOperator = userOperatorFactory.getUserByEmail(session,
          request.getEmail());
      // If user already exists, throw.
      if (userOperator != null) {
        App.SignUpResponse alreadyExistResponse =
            App.SignUpResponse.newBuilder()
                .setAlreadyExists(true)
                .build();
        try {
          return new ResponseEntity<>(
              JsonFormat.printer().print(alreadyExistResponse),
              HttpStatus.CONFLICT);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      // Lets create the user.
      userOperatorFactory.create(session,
          request.getFirstName(),
          request.getLastName(),
          request.getEmail(),
          request.getPassword(),
          request.getPhoneNumber());
      return null;
    });

    if (response != null) {
      return response;
    }

    // Add token in the header since user is authorized.
    String jwtToken = JWTAuthenticationManager.getJwtToken(request.getEmail());
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(HEADER_STRING, TOKEN_PREFIX + jwtToken);
    return new ResponseEntity<>("", responseHeaders, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public App.LoginResponse login(@RequestBody App.LoginRequest loginRequest) {
    // Dummy does not do anything. JWTAuthenticationFilter does the work.
    return App.LoginResponse.newBuilder().build();
  }
}
