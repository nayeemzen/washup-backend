package com.washup.app.api.v1.users;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.api.v1.ApiConstants.API_URL;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.users.AddressOperator;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AddressController.URL)
public class AddressController {

  static final String URL = API_URL + "/address";

  @Autowired
  Transacter transacter;

  @Autowired
  AddressOperator.Factory addressOperatorFactory;

  @Autowired
  UserOperator.Factory userOperatorFactory;

  @PostMapping("/set-address")
  public App.SetAddressResponse SetAddress(
      @RequestBody App.SetAddressRequest request,
      Authentication authentication) {
    App.Address address = request.getAddress();
    ParametersChecker.check(address != null, "address is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(address.getStreetAddress()),
        "street_address is missing");
    ParametersChecker.check(!Strings.isNullOrEmpty(address.getPostalCode()),
        "postal_code is missing");

    App.Address updatedAddress = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session,
          authentication);
      checkState(user != null);
      AddressOperator addressOperator =
          addressOperatorFactory.get(session, user.getId());
      if (addressOperator == null) {
        addressOperator = addressOperatorFactory.create(session,
            user.getId(),
            address.getStreetAddress(),
            address.getApt(),
            address.getPostalCode(),
            address.getNotes());
      } else {
        addressOperator.setStreetAddress(address.getStreetAddress())
            .setApt(address.getApt())
            .setPostalCode(address.getPostalCode())
            .setNotes(address.getNotes())
            .update();
      }
      return addressOperator.toProto();
    });
    return App.SetAddressResponse.newBuilder()
        .setAddress(updatedAddress)
        .build();
  }

  @GetMapping("/get-address")
  public App.GetAddressResponse getAddress(Authentication authentication) {
    App.Address address = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session,
          authentication);
      AddressOperator addressOperator =
          addressOperatorFactory.get(session, user.getId());
      return addressOperator != null
          ? addressOperator.toProto()
          : null;
    });
    return App.GetAddressResponse.newBuilder()
        .setAddress(address)
        .build();
  }
}
