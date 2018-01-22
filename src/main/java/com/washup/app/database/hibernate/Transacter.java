package com.washup.app.database.hibernate;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Nullable;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.stereotype.Service;

@Service
public class Transacter {

  static final ThreadLocal<Session> CURRENT_SESSION = new ThreadLocal<>();

  SessionFactory sessionFactory;

  public Transacter(EntityManagerFactory entityManagerFactory) {
    if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
      throw new IllegalArgumentException("Factory is not hibernate factory");
    }
    sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
  }

  public <T> T call(final DatabaseWork<T> databaseWork) {
    checkState(getCurrentSession() == null,
        "Already in a session! Nested sessions not allowed yet.");
    final Session session;
    try {
      session = getSessionBuilder().openSession();
      CURRENT_SESSION.set(session);
      ReturningWork<T> returningWork = connection -> {
        Transaction transaction = null;
        try {
          connection.setReadOnly(false);
          transaction = session.beginTransaction();
          T result = databaseWork.work(session);
          session.flush();
          connection.commit();
          return result;
        } catch (Throwable e) {
          if (transaction != null) {
            connection.rollback();
          }
          throw new RuntimeException(e);
        }
      };
      return session.doReturningWork(returningWork);
    } finally {
      if (getCurrentSession() != null) {
        CURRENT_SESSION.get().close();
        CURRENT_SESSION.remove();
      }
    }
  }

  public void execute(final VoidDatabaseWork databaseWork) {
    call(session -> {
      databaseWork.work(session);
      return null;
    });
  }

  private @Nullable
  Session getCurrentSession() {
    // TODO: add some session validation like readonly transactions not nested
    // inside of RW transaction.
    return CURRENT_SESSION.get();
  }

  private SessionBuilder getSessionBuilder() {
    return sessionFactory.withOptions();
  }
}
