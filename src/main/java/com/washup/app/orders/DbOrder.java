package com.washup.app.orders;

import com.washup.app.database.hibernate.TimestampEntity;
import org.hibernate.Session;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.util.Date;

import static com.google.common.base.Preconditions.checkState;

@Entity(name = "orders")
@Table(name = "orders")
public class DbOrder extends TimestampEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  //TODO: add converters to type
  private String token;

  private String idempotenceToken;

  private long userId;

  private String orderType;

  //TODO: add converters to type
  private String status;

  private long totalCostCents;

  @Type(type="timestamp")
  private Date pickupDate;

  @Type(type="timestamp")
  private Date deliveryDate;

  private boolean rushService;

  long getId() {
    return id;
  }

  public OrderToken getToken() {
    return OrderToken.of(token);
  }

  public String getOrderType() {
    return orderType;
  }

  public long getUserId() {
    return userId;
  }

  public String getStatus() {
    return status;
  }

  public String getIdempotenceToken() {
    return idempotenceToken;
  }

  public DateTime getPickupDate() {
    return new DateTime(deliveryDate);
  }

  public DateTime getDeliveryDate() {
    return new DateTime(deliveryDate);
  }

  public long getTotalCostCents() {
    return totalCostCents;
  }

  public boolean isRushService() {
    return rushService;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setTotalCostCents(long totalCostCents) {
    this.totalCostCents = totalCostCents;
  }

  public void setRushService(boolean rushService) {
    this.rushService = rushService;
  }

  static DbOrder create(Session session, long userId, String idempotentToken,
     String orderType, String status, DateTime deliveryDate,
     DateTime pickupDate) {
    checkState(deliveryDate.isAfter(pickupDate));
    DbOrder order = new DbOrder();
    order.token = OrderToken.generate().rawToken();
    order.orderType = orderType;
    order.idempotenceToken = idempotentToken;
    order.userId = userId;
    order.status = status;
    order.totalCostCents = 0L;
    order.pickupDate = pickupDate.toDate();
    order.deliveryDate = pickupDate.toDate();
    Days days = Days.daysBetween(pickupDate, deliveryDate);
    order.rushService = days == Days.ONE || days == Days.ZERO;

    try {
      session.save(order);
    } catch (ConstraintViolationException e) {
      //LOG
      throw e;
    }

    return order;
  }
}
