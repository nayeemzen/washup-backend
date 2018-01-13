package com.washup.app.api.v1.users;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.washup.app.api.v1.ApiConstants.API_URL;

@RestController
@RequestMapping(UserController.URL)
public class UserController {
  static final String URL = API_URL + "/users";

  @Autowired Transacter transacter;
  @Autowired UserOperator.Factory userOperatorFactory;

  @PostMapping("/sign-up")
  public App.SignUpResponse signUp(@RequestBody App.SignUpRequest request) {
    return transacter.call(session -> {
      UserOperator userOperator = userOperatorFactory.create(session,
          request.getFirstName(),
          request.getLastName(),
          request.getEmail(),
          request.getPassword(),
          request.getPhoneNumber());
      return App.SignUpResponse.newBuilder().build();
    });
  }

  @PostMapping("/login")
  public App.LoginResponse login(@RequestBody App.LoginRequest loginRequest) {
    // Dummy does not do anything. JWTAuthenticationFilter does the work.
    return App.LoginResponse.newBuilder().build();
  }
}
