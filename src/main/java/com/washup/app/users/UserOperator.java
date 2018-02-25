package com.washup.app.users;

import static com.google.common.base.Preconditions.checkArgument;
import static org.hibernate.criterion.Restrictions.eq;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.AbstractOperator;
import com.washup.app.database.hibernate.Id;
import com.washup.protos.App;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

public class UserOperator extends AbstractOperator<DbUser> {
  public UserOperator(Session session, DbUser user) {
    super(session, user);
  }

  public Id<DbUser> getId() {
    return entity.getId();
  }

  public DbUser getUser() {
    return entity;
  }

  public String getEncodedPassword() {
    return entity.getEncodedPassword();
  }

  public String getEmail() {
    return entity.getEmail();
  }

  public String getPhoneNumber() {
    return entity.getPhoneNumber();
  }

  public UserOperator setFirstName(String firstName) {
    checkArgument(!Strings.isNullOrEmpty(firstName));
    entity.setFirstName(firstName);
    return this;
  }

  public UserOperator setLastName(String lastName) {
    checkArgument(!Strings.isNullOrEmpty(lastName));
    entity.setLastName(lastName);
    return this;
  }

  public UserOperator setPhoneNumber(String phoneNumber) {
    checkArgument(!Strings.isNullOrEmpty(phoneNumber));
    entity.setPhoneNumber(phoneNumber);
    return this;
  }

  public UserOperator update() {
    session.update(entity);
    return this;
  }

  public App.User toProto() {
    return entity.toProto();
  }

  @Component
  public static class Factory {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PreferenceOperator.Factory preferenceOperatorFactory;

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
      preferenceOperatorFactory.createWithDefault(session, dbUser.getId());
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

    public UserOperator getAuthenticatedUser(Session session, Authentication authentication) {
      UserOperator user = getUserByEmail(session, (String) authentication.getPrincipal());
      if (user == null) {
        throw new AuthenticationCredentialsNotFoundException("Could not find user.");
      }

      return user;
    }
  }
}
