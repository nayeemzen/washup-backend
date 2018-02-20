package com.washup.app.pricing;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.App.Pricing;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity(name = "item_pricings")
@Table(name = "item_pricings")
public class DbItemPricing extends TimestampEntity implements IdEntity {
  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String item;

  @Column(nullable = false)
  private Long priceCents;

  @Column(nullable = false)
  private Long bucketId;

  public Id<DbItemPricing> getId() {
    return new Id<>(id);
  }

  public String getItem() {
    return item;
  }

  public Long getPriceCents() {
    return priceCents;
  }

  public Long getBucketId() {
    return bucketId;
  }

  public Pricing toProto() {
    return Pricing.newBuilder()
        .setItem(item)
        .setPriceCents(priceCents)
        .build();
  }
}
