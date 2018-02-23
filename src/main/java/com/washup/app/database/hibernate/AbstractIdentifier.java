package com.washup.app.database.hibernate;

import java.io.Serializable;
import java.util.Objects;

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

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AbstractIdentifier && Objects.equals(id, ((AbstractIdentifier) obj).id);
  }
}
