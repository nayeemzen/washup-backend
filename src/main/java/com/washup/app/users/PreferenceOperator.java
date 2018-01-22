package com.washup.app.users;

import com.washup.app.database.hibernate.Id;
import com.washup.protos.App;
import com.washup.protos.App.Preference;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

public class PreferenceOperator {

  private final static App.Preference DEFAULT_PREFERENCE = Preference.newBuilder()
      .setScented(false)
      .setFabricSoftener(true)
      .setOneDayDelivery(false)
      .setLaundryReminder(true)
      .build();

  private final Session session;
  private final DbPreference preference;

  public PreferenceOperator(Session session, DbPreference preference) {
    this.session = session;
    this.preference = preference;
  }

  public Id<DbPreference> getId() {
    return preference.getId();
  }

  public Boolean getScented() {
    return preference.getScented();
  }

  public PreferenceOperator setScented(@Nullable Boolean scented) {
    if (scented == null) {
      return this;
    }
    preference.setScented(scented);
    return this;
  }

  public Boolean getFabricSoftener() {
    return preference.getFabricSoftener();
  }

  public PreferenceOperator setFabricSoftener(@Nullable Boolean fabricSoftener) {
    if (fabricSoftener == null) {
      return this;
    }
    preference.setFabricSoftener(fabricSoftener);
    return this;
  }

  public Boolean getOneDayDelivery() {
    return preference.getOneDayDelivery();
  }

  public PreferenceOperator setOneDayDelivery(@Nullable Boolean oneDayDelivery) {
    if (oneDayDelivery == null) {
      return null;
    }
    preference.setOneDayDelivery(oneDayDelivery);
    return this;
  }

  public Boolean getLaundryReminder() {
    return preference.getLaundryReminder();
  }

  public PreferenceOperator setLaundryReminder(@Nullable Boolean laundryReminder) {
    if (laundryReminder == null) {
      return this;
    }
    preference.setLaundryReminder(laundryReminder);
    return this;
  }

  public PreferenceOperator update() {
    session.update(preference);
    return this;
  }

  public App.Preference toProto() {
    return preference.toProto();
  }

  @Component
  public static class Factory {

    public @Nullable
    PreferenceOperator get(Session session, Id<DbUser> userId) {
      DbPreference existingPreference = DbPreference.get(session, userId);
      return existingPreference != null
          ? new PreferenceOperator(session, existingPreference)
          : null;
    }

    public PreferenceOperator createWithDefault(Session session, Id<DbUser> userId) {
      DbPreference address = DbPreference.create(session, userId, DEFAULT_PREFERENCE.getScented(),
          DEFAULT_PREFERENCE.getFabricSoftener(), DEFAULT_PREFERENCE.getOneDayDelivery(),
          DEFAULT_PREFERENCE.getLaundryReminder());
      return new PreferenceOperator(session, address);
    }
  }
}
