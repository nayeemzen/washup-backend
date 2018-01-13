package com.washup.app.database.hibernate;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class TimestampEntity {
  @Column(updatable = false)
  @Type(type="timestamp")
  private Date createdAt;

  @Column
  @Type(type="timestamp")
  private Date updatedAt;
}
