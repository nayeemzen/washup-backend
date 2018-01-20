package com.washup.app.users;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Id;
import com.washup.protos.App;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.hibernate.criterion.Restrictions.eq;

public class UserOperator {

  private final Session session;
  private final DbUser user;

  public UserOperator(Session session, DbUser user) {
    this.session = session;
    this.user = user;
  }

  public Id<DbUser> getId() {
    return user.getId();
  }

  public String getEncodedPassword() {
    return user.getEncodedPassword();
  }

  public UserOperator setFirstName(String firstName) {
    checkArgument(!Strings.isNullOrEmpty(firstName));
    user.setFirstName(firstName);
    return this;
  }

  public UserOperator setLastName(String lastName) {
    checkArgument(!Strings.isNullOrEmpty(lastName));
    user.setLastName(lastName);
    return this;
  }

  public UserOperator setPhoneNumber(String phoneNumber) {
    checkArgument(!Strings.isNullOrEmpty(phoneNumber));
    user.setPhoneNumber(phoneNumber);
    return this;
  }

  public UserOperator update() {
    session.update(user);
    return this;
  }

  public App.User toProto() {
    return user.toProto();
  }

  @Component
  public static class Factory {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserOperator create(
        Session session,
        String firstName,
        @Nullable String lastName,
        String email,
        String rawPassword,
        String phoneNumber) {
      // Make sure rawPassword is hashed
      String password = bCryptPasswordEncoder.encode(rawPassword);
      DbUser dbUser = DbUser.create(session, firstName, lastName, email,
          password, phoneNumber);
      return new UserOperator(session, dbUser);
    }

    public UserOperator get(Session session, UserToken userToken) {
      DbUser dbUser = (DbUser) session.createCriteria(DbUser.class)
          .add(eq("token", userToken.getId()))
          .uniqueResult();
      return dbUser != null ? new UserOperator(session, dbUser) : null;
    }

    public UserOperator getUserByEmail(Session session, String email) {
      DbUser dbUser = (DbUser) session.createCriteria(DbUser.class)
          .add(eq("email", email))
          .uniqueResult();
      return dbUser != null ? new UserOperator(session, dbUser) : null;
    }

    public UserOperator getAuthenticatedUser(Session session,
        Authentication authentication) {
      UserOperator user =
          getUserByEmail(session, (String) authentication.getPrincipal());
      checkState(user != null);
      return user;
    }
  }
}
