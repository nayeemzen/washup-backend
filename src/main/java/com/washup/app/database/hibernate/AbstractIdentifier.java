package com.washup.app.database.hibernate;

import java.io.Serializable;

public abstract class AbstractIdentifier<T> implements Serializable {

  private static final long serialVersionUID = 841923710740919047L;

  private final T id;

  protected AbstractIdentifier(T id) {
    this.id = id;
  }

  public T getId() {
    return id;
  }

  // Returns string version of the current identifier.
  @Override
  public String toString() {
    return String.valueOf(id);
  }
}
