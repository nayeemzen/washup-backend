package com.washup.app.api.v1.users;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.api.v1.ApiConstants.API_URL;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.users.PreferenceOperator;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import com.washup.protos.App.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PreferenceController.URL)
public class PreferenceController {

  static final String URL = API_URL + "/users";

  @Autowired
  Transacter transacter;

  @Autowired
  PreferenceOperator.Factory preferenceOperatorFactory;

  @Autowired
  UserOperator.Factory userOperatorFactory;

  @PostMapping("/set-preferences")
  public App.SetPreferenceResponse SetPreference(@RequestBody App.SetPreferenceRequest request,
      Authentication authentication) {
    Preference preference = request.getPreference();
    ParametersChecker.check(preference != null, "preference is missing");

    App.Preference updatePreference = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      checkState(user != null);
      PreferenceOperator preferenceOperator = preferenceOperatorFactory.get(session, user.getId());
      checkState(preferenceOperator != null);
      preferenceOperator.setScented(preference.getScented())
          .setFabricSoftener(preference.getFabricSoftener())
          .setOneDayDelivery(preference.getOneDayDelivery())
          .setLaundryReminder(preference.getLaundryReminder())
          .update();
      return preferenceOperator.toProto();
    });
    return App.SetPreferenceResponse.newBuilder()
        .setPreference(updatePreference)
        .build();
  }

  @GetMapping("/get-preferences")
  public App.GetPreferenceResponse getPreferences(Authentication authentication) {
    App.Preference preference = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      PreferenceOperator preferenceOperator = preferenceOperatorFactory.get(session, user.getId());
      return preferenceOperator != null
          ? preferenceOperator.toProto()
          : null;
    });
    return App.GetPreferenceResponse.newBuilder()
        .setPreference(preference)
        .build();
  }
}
