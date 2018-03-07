package com.washup.app.users;

import com.washup.app.common.PostalCode;
import com.washup.app.database.hibernate.Id;
import com.washup.protos.App;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

public class AddressOperator {

  private final Session session;
  private final DbAddress address;

  public AddressOperator(Session session, DbAddress address) {
    this.session = session;
    this.address = address;
  }

  public Id<DbAddress> getId() {
    return address.getId();
  }

  public AddressOperator setStreetAddress(String streetAddress) {
    address.setStreetAddress(streetAddress);
    return this;
  }

  public AddressOperator setApt(@Nullable String apt) {
    address.setApt(apt);
    return this;
  }

  public AddressOperator setPostalCode(String postalCode) {
    address.setPostalCode(postalCode);
    return this;
  }

  public AddressOperator setNotes(@Nullable String notes) {
    address.setNotes(notes);
    return this;
  }

  public String getPostalCode() {
    return address.getPostalCode();
  }

  public AddressOperator update() {
    session.update(address);
    return this;
  }

  public App.Address toProto() {
    return address.toProto();
  }

  @Component
  public static class Factory {

    public @Nullable
    AddressOperator get(
        Session session,
        Id<DbUser> userId) {
      DbAddress existingAddress = DbAddress.get(session, userId);
      return existingAddress != null
          ? new AddressOperator(session, existingAddress)
          : null;
    }

    public AddressOperator create(
        Session session,
        Id<DbUser> userId,
        String streetAddress,
        @Nullable String apt,
        PostalCode postalCode,
        @Nullable String notes) {
      DbAddress address = DbAddress.create(session, userId, streetAddress, apt,
          postalCode, notes);
      return new AddressOperator(session, address);
    }
  }
}
