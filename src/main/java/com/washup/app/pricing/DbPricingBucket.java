package com.washup.app.pricing;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity(name = "pricing_buckets")
@Table(name = "pricing_buckets")
public class DbPricingBucket extends TimestampEntity implements IdEntity {
  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String bucketName;

  public Id<DbPricingBucket> getId() {
    return new Id<>(id);
  }
}
