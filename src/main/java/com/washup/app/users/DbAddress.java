package com.washup.app.users;

import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.App;
import org.hibernate.Session;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.ConstraintViolationException;

import static org.hibernate.criterion.Restrictions.eq;

@Entity(name = "addresses")
@Table(name = "addresses")
public class DbAddress extends TimestampEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long userId;

  private String streetAddress;

  private String apt;

  private String postalCode;

  private String notes;

  public long getId() {
    return id;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public String getApt() {
    return apt;
  }

  String getPostalCode() {
    return postalCode;
  }

  String getNotes() {
    return notes;
  }

  DbAddress setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
    return this;
  }

  DbAddress setApt(@Nullable String apt) {
    this.apt = apt;
    return this;
  }

  DbAddress setPostalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  DbAddress setNotes(@Nullable String notes) {
    this.notes = notes;
    return this;
  }

  App.Address toProto() {
    return App.Address.newBuilder()
        .setStreetAddress(streetAddress)
        .setApt(apt)
        .setPostalCode(postalCode)
        .setNotes(notes)
        .build();
  }

  static @Nullable DbAddress get(Session session, long userId) {
    return (DbAddress) session.createCriteria(DbAddress.class)
        .add(eq("userId", userId))
        .uniqueResult();
  }

  static DbAddress create(Session session,
                          long userId,
                          String streetAddress,
                          @Nullable String apt,
                          String postalCode,
                          @Nullable String notes) {
    DbAddress dbAddress = new DbAddress();
    dbAddress.userId = userId;
    dbAddress.streetAddress = streetAddress;
    dbAddress.apt = apt;
    dbAddress.postalCode = postalCode;
    dbAddress.notes = notes;

    try {
      session.save(dbAddress);
    } catch (ConstraintViolationException e) {
      //LOG
      throw e;
    }

    return dbAddress;
  }
}
