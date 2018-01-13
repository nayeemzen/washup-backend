package com.washup.app.users;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static org.hibernate.criterion.Restrictions.eq;

public class UserOperator {
  private final Session session;
  private final DbUser user;

  public UserOperator(Session session, DbUser user) {
    this.session = session;
    this.user = user;
  }

  public String getEncodedPassword() {
    return user.getEncodedPassword();
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
      DbUser dbUser = DbUser.create(session, UserToken.generate(), firstName,
          lastName, email, password, phoneNumber);
      return new UserOperator(session, dbUser);
    }

    public UserOperator get(Session session, UserToken userToken) {
      DbUser dbUser = (DbUser) session.createCriteria(DbUser.class)
          .add(eq("token", userToken.rawToken()))
          .uniqueResult();
      return dbUser != null ? new UserOperator(session, dbUser) : null;
    }

    public UserOperator getUserByEmail(Session session, String email) {
      DbUser dbUser = (DbUser) session.createCriteria(DbUser.class)
          .add(eq("email", email))
          .uniqueResult();
      return dbUser != null ? new UserOperator(session, dbUser) : null;
    }
  }
}
