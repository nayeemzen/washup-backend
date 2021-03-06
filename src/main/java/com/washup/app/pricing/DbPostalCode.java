package com.washup.app.pricing;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity(name = "postal_codes")
@Table(name = "postal_codes")
public class DbPostalCode extends TimestampEntity implements IdEntity {
  public enum Rules {
    STARTS_WITH,
    FULL_MATCH,
  }

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String postalCode;

  @Column(nullable = false)
  private String rule;

  @Column(nullable = false)
  private Long bucketId;

  public Id<DbPostalCode> getId() {
    return new Id<>(id);
  }

  public Id<DbPricingBucket> getBucketId() {
    return new Id<>(bucketId);
  }
}
