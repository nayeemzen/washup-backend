package com.washup.app.users;

import static com.google.common.base.Preconditions.checkNotNull;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.Transacter;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class UserTester {

  private final Transacter transacter;
  private final Id<DbUser> userId;

  public UserTester(Factory factory, Id<DbUser> userId) {
    this.transacter = factory.transacter;
    this.userId = checkNotNull(userId);
  }

  public Id<DbUser> getId() {
    return userId;
  }

  public UserToken getToken() {
    return transacter.call(session -> load(session).getToken());
  }

  private DbUser load(Session session) {
    return session.load(DbUser.class, userId.getId());
  }

  @Component
  public static class Factory {
    @Autowired
    Transacter transacter;

    @Autowired
    UserQuery.Factory userQueryFactory;

    public @Nullable UserTester get(String email) {
      Id<DbUser> userId = transacter.call(session -> {
        UserOperator userOperator = userQueryFactory.get(session)
            .email(email)
            .uniqueResult();
        return userOperator != null ? userOperator.getId() : null;
      });
      return userId != null ? new UserTester(this, userId) : null;
    }
  }
}
