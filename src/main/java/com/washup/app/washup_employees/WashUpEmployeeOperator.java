package com.washup.app.washup_employees;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.hibernate.criterion.Restrictions.eq;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Id;
import com.washup.app.users.DbUser;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

public class WashUpEmployeeOperator {

  private final Session session;
  private final DbWashUpEmployee washUpEmployee;

  public WashUpEmployeeOperator(Session session, DbWashUpEmployee washUpEmployee) {
    this.session = session;
    this.washUpEmployee = washUpEmployee;
  }

  public Id<DbWashUpEmployee> getId() {
    return washUpEmployee.getId();
  }

  public String getEncodedPassword() {
    return washUpEmployee.getEncodedPassword();
  }

  public WashUpEmployeeToken getToken() {
    return washUpEmployee.getToken();
  }

  public WashUpEmployeeOperator setFirstName(String firstName) {
    checkArgument(!Strings.isNullOrEmpty(firstName));
    washUpEmployee.setFirstName(firstName);
    return this;
  }

  public WashUpEmployeeOperator setLastName(String lastName) {
    checkArgument(!Strings.isNullOrEmpty(lastName));
    washUpEmployee.setLastName(lastName);
    return this;
  }

  public WashUpEmployeeOperator update() {
    session.update(washUpEmployee);
    return this;
  }

  @Component
  public static class Factory {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public WashUpEmployeeOperator create(Session session, String firstName,
        @Nullable String lastName, String email, String rawPassword) {
      // Make sure rawPassword is hashed
      String password = bCryptPasswordEncoder.encode(rawPassword);
      DbWashUpEmployee employee = DbWashUpEmployee.create(session, firstName, lastName, email,
          password);
      return new WashUpEmployeeOperator(session, employee);
    }

    public WashUpEmployeeOperator get(Session session, WashUpEmployeeToken employeeToken) {
      DbWashUpEmployee washUpEmployee = (DbWashUpEmployee) session.createCriteria(DbUser.class)
          .add(eq("token", employeeToken.getId()))
          .uniqueResult();
      return washUpEmployee != null ? new WashUpEmployeeOperator(session, washUpEmployee) : null;
    }

    public WashUpEmployeeOperator getWashUpEmployeeByEmail(Session session, String email) {
      DbWashUpEmployee dbWashUpEmployee =
          (DbWashUpEmployee) session.createCriteria(DbWashUpEmployee.class)
              .add(eq("email", email))
              .uniqueResult();
      return dbWashUpEmployee != null
          ? new WashUpEmployeeOperator(session, dbWashUpEmployee)
          : null;
    }

    public WashUpEmployeeOperator getAuthenticatedEmployee(Session session,
        Authentication authentication) {
      WashUpEmployeeOperator user = getWashUpEmployeeByEmail(session,
          (String) authentication.getPrincipal());
      checkState(user != null);
      return user;
    }
  }
}
