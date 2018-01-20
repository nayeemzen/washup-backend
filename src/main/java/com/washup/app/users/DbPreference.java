package com.washup.app.users;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.App;
import com.washup.protos.App.Preference;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;

@Entity(name = "preferences")
@Table(name = "preferences")
public class DbPreference extends TimestampEntity implements IdEntity {

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private Boolean scented;

  private Boolean fabricSoftener;

  private Boolean oneDayDelivery;

  private Boolean laundryReminder;

  public Id<DbPreference> getId() {
    return new Id<>(id);
  }

  public Id<DbUser> getUserId() {
    return new Id<>(userId);
  }

  public Boolean getScented() {
    return scented;
  }

  public void setScented(Boolean scented) {
    this.scented = scented;
  }

  public Boolean getFabricSoftener() {
    return fabricSoftener;
  }

  public void setFabricSoftener(Boolean fabricSoftener) {
    this.fabricSoftener = fabricSoftener;
  }

  public Boolean getOneDayDelivery() {
    return oneDayDelivery;
  }

  public void setOneDayDelivery(Boolean oneDayDelivery) {
    this.oneDayDelivery = oneDayDelivery;
  }

  public Boolean getLaundryReminder() {
    return laundryReminder;
  }

  public void setLaundryReminder(Boolean laundryReminder) {
    this.laundryReminder = laundryReminder;
  }

  public App.Preference toProto() {
    return Preference.newBuilder()
        .setScented(scented)
        .setFabricSoftener(fabricSoftener)
        .setOneDayDelivery(oneDayDelivery)
        .setLaundryReminder(laundryReminder)
        .build();
  }

  static @Nullable
  DbPreference get(Session session, Id<DbUser> userId) {
    return (DbPreference) session.createCriteria(DbPreference.class)
        .add(eq("userId", userId.getId()))
        .uniqueResult();
  }

  static DbPreference create(Session session, Id<DbUser> userId, @Nullable Boolean scented,
      @Nullable Boolean fabricSoftener, @Nullable Boolean oneDayDelivery,
      @Nullable Boolean laundryReminder) {
    DbPreference dbPreference = new DbPreference();
    dbPreference.userId = userId.getId();
    dbPreference.scented = scented;
    dbPreference.fabricSoftener = fabricSoftener;
    dbPreference.oneDayDelivery = oneDayDelivery;
    dbPreference.laundryReminder = laundryReminder;

    try {
      session.save(dbPreference);
    } catch (ConstraintViolationException e) {
      //LOG
      throw e;
    }

    return dbPreference;
  }
}
