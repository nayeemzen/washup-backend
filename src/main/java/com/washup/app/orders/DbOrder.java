package com.washup.app.orders;

import static com.google.common.base.Preconditions.checkState;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.app.users.DbUser;
import com.washup.app.users.UserOperator;
import com.washup.protos.Shared;
import com.washup.protos.Shared.Order;
import com.washup.protos.Shared.OrderStatus;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Days;

@Entity(name = "orders")
@Table(name = "orders")
public class DbOrder extends TimestampEntity implements IdEntity {

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //TODO: add converters to type
  private String token;

  private String idempotenceToken;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @Fetch(FetchMode.SELECT)
  private DbUser user;

  @Column(name = "user_id", updatable = false, insertable = false, nullable = false)
  private Long userId;

  private String orderType;

  //TODO: add converters to type
  private String status;

  private long totalCostCents;

  @Type(type = "timestamp")
  private Date pickupDate;

  @Type(type = "timestamp")
  private Date deliveryDate;

  @Type(type = "timestamp")
  private Date billedAt;

  private boolean rushService;

  @Override
  public Id<DbOrder> getId() {
    return new Id<>(id);
  }

  OrderToken getToken() {
    return OrderToken.of(token);
  }

  String getOrderType() {
    return orderType;
  }

  Id<DbUser> getUserId() {
    return new Id<>(userId);
  }

  String getStatus() {
    return status;
  }

  String getIdempotenceToken() {
    return idempotenceToken;
  }

  DateTime getPickupDate() {
    return new DateTime(deliveryDate);
  }

  DateTime getDeliveryDate() {
    return new DateTime(deliveryDate);
  }

  Date getBilledAt() {
    return billedAt;
  }

  DbOrder setBilledAt(Date billedAt) {
    this.billedAt = billedAt;
    return this;
  }

  long getTotalCostCents() {
    return totalCostCents;
  }

  boolean isRushService() {
    return rushService;
  }

  void setStatus(String status) {
    this.status = status;
  }

  void setTotalCostCents(long totalCostCents) {
    this.totalCostCents = totalCostCents;
  }

  void setRushService(boolean rushService) {
    this.rushService = rushService;
  }

  public Shared.Order toWire() {
    return Order.newBuilder()
        .setUserToken(user.getToken().getId())
        .setToken(token)
        .setStatus(OrderStatus.valueOf(status))
        .setDeliveryDate(deliveryDate.getTime())
        .setPickupDate(pickupDate.getTime())
        .setBilledAt(billedAt != null ? billedAt.getTime() : 0)
        .setRushService(rushService)
        .setTotalCostCents(totalCostCents)
        .setCreatedAt(getCreatedAt().getTime())
        .setUpdatedAt(getUpdatedAt().getTime())
        .build();
  }

  static DbOrder create(Session session, UserOperator user, String idempotentToken,
      String orderType, String status, DateTime deliveryDate,
      DateTime pickupDate) {
    checkState(deliveryDate.isAfter(pickupDate));
    DbOrder order = new DbOrder();
    order.token = OrderToken.generate().getId();
    order.orderType = orderType;
    order.idempotenceToken = idempotentToken;
    order.user = user.getUser();
    order.userId = user.getId().getId();
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
