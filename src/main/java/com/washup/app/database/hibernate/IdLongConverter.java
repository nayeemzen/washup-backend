package com.washup.app.database.hibernate;

import javax.persistence.AttributeConverter;

public class IdLongConverter<T extends IdEntity> implements AttributeConverter<Id<T>, Long> {

  @Override
  public Long convertToDatabaseColumn(Id id) {
    return (Long) id.getId();
  }

  @Override
  public Id<T> convertToEntityAttribute(Long aLong) {
    return new Id<>(aLong);
  }
}
