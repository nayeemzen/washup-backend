package com.washup.app.washup_employees;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.StoreAsString;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.app.users.UserToken;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;

@Entity(name = "washup_employees")
@Table(name = "washup_employees")
public class DbWashUpEmployee extends TimestampEntity implements IdEntity {

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @StoreAsString
  private String token;

  private String firstName;

  private String lastName;

  @Column(updatable = false)
  private String email;

  private String password;

  @Override
  public Id<DbWashUpEmployee> getId() {
    return new Id<>(id);
  }

  public WashUpEmployeeToken getToken() {
    return new WashUpEmployeeToken(token);
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

  static DbWashUpEmployee create(Session session, String firstName, @Nullable String lastName,
      String email, String hashedPassword) {
    DbWashUpEmployee washUpEmployee = new DbWashUpEmployee();
    washUpEmployee.token = WashUpEmployeeToken.generate().getId();
    washUpEmployee.firstName = firstName;
    washUpEmployee.lastName = lastName;
    washUpEmployee.email = email;
    washUpEmployee.password = hashedPassword;

    try {
      session.save(washUpEmployee);
    } catch (ConstraintViolationException e) {
      //LOG
      throw e;
    }

    return washUpEmployee;
  }
}
