package com.washup.app.api.v1.users;

import static com.washup.app.api.v1.ApiConstants.API_URL;
import static com.washup.app.configuration.SecurityConstants.HEADER_STRING;
import static com.washup.app.configuration.SecurityConstants.TOKEN_PREFIX;

import com.google.common.base.Strings;
import com.washup.app.authentication.JWTAuthenticationManager;
import com.washup.app.common.EmailValidator;
import com.washup.app.common.Normalizer;
import com.washup.app.common.PhoneNumber;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.notifications.email.EmailNotificationService;
import com.washup.app.notifications.email.Emails;
import com.washup.app.pricing.PostalCodeOperator;
import com.washup.app.users.AddressOperator;
import com.washup.app.users.PaymentCardOperator;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import com.washup.protos.App.GetProfileResponse;
import com.washup.protos.App.GetProfileResponse.Builder;
import com.washup.protos.App.ServiceAvailability;
import com.washup.protos.App.SignUpResponse;
import com.washup.protos.App.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserController.URL)
public class UserController {
  static final String URL = API_URL + "/users";

  @Autowired Transacter transacter;
  @Autowired UserOperator.Factory userOperatorFactory;
  @Autowired EmailNotificationService emailNotificationService;
  @Autowired PaymentCardOperator.Factory paymentCardOperatorFactory;
  @Autowired AddressOperator.Factory addressOperatorFactory;
  @Autowired PostalCodeOperator.Factory postalCodeOperatorFactory;
  @Autowired Normalizer normalizer;
  @Autowired EmailValidator emailValidator;

  @PostMapping("/sign-up")
  public ResponseEntity<SignUpResponse> signUp(
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

    String emailAddress = normalizer.removeSpaces(request.getEmail());
    ParametersChecker.check(emailValidator.isValid(emailAddress), "email is invalid");

    String phoneNumber = normalizer.removeSpaces(request.getPhoneNumber());
    ParametersChecker.check(PhoneNumber.isValid(phoneNumber), "phone_number is invalid");

    ResponseEntity<SignUpResponse> response = transacter.call(session -> {
      UserOperator userOperator = userOperatorFactory.getUserByEmail(session, request.getEmail());
      // If user already exists, throw.
      if (userOperator != null) {
        App.SignUpResponse alreadyExistResponse = App.SignUpResponse.newBuilder()
            .setAlreadyExists(true)
            .build();
        try {
          return new ResponseEntity<>(alreadyExistResponse, HttpStatus.OK);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

      // Lets create the user.

      userOperatorFactory.create(session,
          request.getFirstName(),
          request.getLastName(),
          emailAddress,
          request.getPassword(),
          PhoneNumber.parse(phoneNumber));
      return null;
    });

    if (response != null) {
      return response;
    }

    // Add token in the header since user is authorized.
    String jwtToken = JWTAuthenticationManager.getJwtToken(request.getEmail());
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set(HEADER_STRING, TOKEN_PREFIX + jwtToken);

    emailNotificationService.sendEmail(Emails.welcomeEmail(User.newBuilder()
        .setFirstName(request.getFirstName())
        .setEmail(request.getEmail())
        .build()));

    return new ResponseEntity<>(SignUpResponse.newBuilder().build(), responseHeaders,
        HttpStatus.CREATED);
  }

  @PostMapping("/set-profile")
  public App.SetProfileResponse setProfile(@RequestBody App.SetProfileRequest request,
      Authentication authentication) {
    App.User user = request.getUser();
    ParametersChecker.check(user != null, "user is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(user.getFirstName()),
        "first_name is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(user.getLastName()),
        "last_name is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(user.getPhoneNumber()),
        "phone_number is missing");
    return transacter.call(session -> {
      UserOperator currentUser = userOperatorFactory.getAuthenticatedUser(
          session, authentication);
      currentUser.setFirstName(user.getFirstName())
          .setLastName(user.getLastName())
          .setPhoneNumber(user.getPhoneNumber())
          .update();
      return App.SetProfileResponse.newBuilder()
          .setUser(currentUser.toProto())
          .build();
    });
  }

  @GetMapping("/get-profile")
  public App.GetProfileResponse getProfile(Authentication authentication) {
    return transacter.call(session -> {
      UserOperator currentUser = userOperatorFactory.getAuthenticatedUser(session, authentication);
      PaymentCardOperator paymentCardOperator = paymentCardOperatorFactory
          .get(session, currentUser.getId());
      AddressOperator addressOperator = addressOperatorFactory.get(session, currentUser.getId());
      Builder builder = GetProfileResponse.newBuilder()
          .setUser(currentUser.toProto());
      if (paymentCardOperator != null) {
        builder.setCard(paymentCardOperator.toProto());
      }
      if (addressOperator != null) {
        builder.setAddress(addressOperator.toProto());
        PostalCodeOperator postalCodeOperator = postalCodeOperatorFactory
            .get(session, addressOperator.getPostalCode());
        builder.setAvailability(postalCodeOperator != null
            ? postalCodeOperator.getAvailibilty()
            : ServiceAvailability.NOT_AVAILABLE);
      }
      return builder
          .build();
    });
  }
}
