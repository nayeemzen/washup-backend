package com.washup.app.database.hibernate;

import org.hibernate.Session;

public abstract class AbstractOperator<T> {
  protected final Session session;
  protected final T entity;

  public AbstractOperator(Session session, T entity) {
    this.session = session;
    this.entity = entity;
  }
}
