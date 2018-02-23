package com.washup.app.orders;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.App.ReceiptItem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(name = "receipt_items")
@Table(name = "receipt_items")
public class DbReceiptItem extends TimestampEntity implements IdEntity {
  private final static Logger logger = LoggerFactory.getLogger(DbReceiptItem.class);

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @Fetch(FetchMode.SELECT)
  private DbOrder order;

  @Column(name = "order_id", updatable = false, insertable = false, nullable = false)
  private Long orderId;

  private String itemName;

  private Long itemAmountCents;

  private Integer itemQuantity;

  @Override
  public Id<DbReceiptItem> getId() {
    return new Id<>(id);
  }

  DbOrder getOrder() {
    return order;
  }

  public ReceiptItem toProto() {
    return ReceiptItem.newBuilder()
        .setItemName(itemName)
        .setItemPriceCents(itemAmountCents)
        .setItemQuatity(itemQuantity)
        .setItemTotalPriceCents(itemAmountCents * itemQuantity)
        .build();
  }
}
