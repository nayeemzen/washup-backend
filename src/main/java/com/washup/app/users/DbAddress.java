package com.washup.app.users;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.Admin.AddressAdmin;
import com.washup.protos.App;
import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;

@Entity(name = "addresses")
@Table(name = "addresses")
public class DbAddress extends TimestampEntity implements IdEntity {

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private String streetAddress;

  private String apt;

  private String postalCode;

  private String notes;

  public Id<DbAddress> getId() {
    return new Id<>(id);
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

  AddressAdmin toInternal() {
    return AddressAdmin.newBuilder()
        .setStreetAddress(streetAddress)
        .setApt(apt)
        .setPostalCode(postalCode)
        .setNotes(notes)
        .build();
  }

  App.Address toProto() {
    return App.Address.newBuilder()
        .setStreetAddress(streetAddress)
        .setApt(apt)
        .setPostalCode(postalCode)
        .setNotes(notes)
        .build();
  }

  static @Nullable
  DbAddress get(Session session, Id<DbUser> userId) {
    return (DbAddress) session.createCriteria(DbAddress.class)
        .add(eq("userId", userId.getId()))
        .uniqueResult();
  }

  static DbAddress create(Session session,
      Id<DbUser> userId,
      String streetAddress,
      @Nullable String apt,
      String postalCode,
      @Nullable String notes) {
    DbAddress dbAddress = new DbAddress();
    dbAddress.userId = userId.getId();
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
