package com.washup.app.users;

import com.washup.app.database.hibernate.AbstractQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

public class UserQuery extends AbstractQuery<DbUser, UserQuery, UserOperator> {
  private UserQuery(Session session) {
    super(session, DbUser.class, UserOperator.class);
  }

  public UserQuery email(String email) {
    criteria.add(Restrictions.eq("email", email));
    return this;
  }

  @Component
  public static class Factory {
    public UserQuery get(Session session) {
      return new UserQuery(session);
    }
  }
}
