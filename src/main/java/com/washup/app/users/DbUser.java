package com.washup.app.users;

import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.App;
import org.hibernate.Session;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.ConstraintViolationException;

@Entity(name = "users")
@Table(name = "users")
public class DbUser extends TimestampEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  //TODO: add converters to type
  private String token;

  private String firstName;

  private String lastName;

  @Column(updatable = false)
  private String email;

  private String password;

  private String phoneNumber;

  long getId() {
    return id;
  }

  public UserToken getToken() {
    return UserToken.of(token);
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  String getEmail() {
    return email;
  }

  String getEncodedPassword() {
    return password;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  void setPassword(String password) {
    this.password = password;
  }

  String getPhoneNumber() {
    return phoneNumber;
  }

  void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  App.User toProto() {
    return App.User.newBuilder()
        .setFirstName(firstName)
        .setLastName(lastName)
        .setPhoneNumber(phoneNumber)
        .build();
  }

  static DbUser create(Session session,
                String firstName,
                @Nullable String lastName,
                String email,
                String hashedPassword,
                String phoneNumber) {
    DbUser dbUser = new DbUser();
    dbUser.token = UserToken.generate().rawToken();
    dbUser.firstName = firstName;
    dbUser.lastName = lastName;
    dbUser.email = email;
    dbUser.password = hashedPassword;
    dbUser.phoneNumber = phoneNumber;

    try {
      session.save(dbUser);
    } catch (ConstraintViolationException e) {
      //LOG
      throw e;
    }

    return dbUser;
  }
}
