package com.washup.app.database.hibernate;

public class Id<T extends IdEntity> extends AbstractIdentifier<Long> {

  public Id(long id) {
    super(id);
  }
}
