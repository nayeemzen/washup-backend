package com.washup.app.database.hibernate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public class TimestampEntity {

  @Column(updatable = false)
  @Type(type = "timestamp")
  @CreationTimestamp
  private Date createdAt;

  @Column
  @Type(type = "timestamp")
  @UpdateTimestamp
  private Date updatedAt;

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
