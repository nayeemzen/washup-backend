package com.washup.app.database.hibernate;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public interface IdEntity {

  Id<? extends IdEntity> getId();
}
