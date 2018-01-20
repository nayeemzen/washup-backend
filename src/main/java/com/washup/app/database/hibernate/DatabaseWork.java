package com.washup.app.database.hibernate;

import org.hibernate.Session;

/**
 * Unit of work that is done during an open session.
 **/
public interface DatabaseWork<T> {

  T work(Session session);
}
