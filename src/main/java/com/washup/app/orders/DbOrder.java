package com.washup.app.orders;

import static com.google.common.base.Preconditions.checkState;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.app.users.DbUser;
import com.washup.app.users.UserOperator;
import com.washup.protos.Admin.OrderAdmin;
import com.washup.protos.App.Order;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(name = "orders")
@Table(name = "orders")
public class DbOrder extends TimestampEntity implements IdEntity {
  private final static Logger logger = LoggerFactory.getLogger(DbOrder.class);

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

  @Column(nullable = false)
  private String orderType;

  //TODO: add converters to type
  @Column(nullable = false)
  private String status;

  private long totalCostCents;

  @Type(type = "timestamp")
  @Column(nullable = false)
  private Date pickupDate;

  @Type(type = "timestamp")
  @Column(nullable = false)
  private Date deliveryDate;

  @Type(type = "timestamp")
  private Date billedAt;

  @Column(nullable = false)
  private boolean rushService;

  @Override
  public Id<DbOrder> getId() {
    return new Id<>(id);
  }

  DbUser getUser() {
    return user;
  }

  OrderToken getToken() {
    return OrderToken.of(token);
  }

  OrderType getOrderType() {
    return OrderType.valueOf(orderType);
  }

  Id<DbUser> getUserId() {
    return new Id<>(userId);
  }

  OrderStatus getStatus() {
    return OrderStatus.valueOf(status);
  }

  String getIdempotenceToken() {
    return idempotenceToken;
  }

  DateTime getPickupDate() {
    return new DateTime(pickupDate);
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

  public OrderAdmin toInternal() {
    return OrderAdmin.newBuilder()
        .setUserToken(user.getToken().getId())
        .setToken(token)
        .setStatus(getStatus())
        .setOrderType(getOrderType())
        .setDeliveryDate(deliveryDate.getTime())
        .setPickupDate(pickupDate.getTime())
        .setBilledAt(billedAt != null ? billedAt.getTime() : 0)
        .setRushService(rushService)
        .setTotalCostCents(totalCostCents)
        .setCreatedAt(getCreatedAt().getTime())
        .setUpdatedAt(getUpdatedAt().getTime())
        .build();
  }
  public Order toProto() {
    return Order.newBuilder()
        .setToken(token)
        .setStatus(getStatus())
        .setOrderType(getOrderType())
        .setDeliveryDate(deliveryDate.getTime())
        .setPickupDate(pickupDate.getTime())
        .setBilledAt(billedAt != null ? billedAt.getTime() : 0)
        .setRushService(rushService)
        .setTotalCostCents(totalCostCents)
        .build();
  }

  static DbOrder create(Session session, UserOperator user, String idempotentToken,
      OrderType orderType, OrderStatus status, DateTime deliveryDate,
      DateTime pickupDate) {
    checkState(deliveryDate.isAfter(pickupDate));
    DbOrder order = new DbOrder();
    order.token = OrderToken.generate().getId();
    order.orderType = orderType.name();
    order.idempotenceToken = idempotentToken;
    order.user = user.getUser();
    order.userId = user.getId().getId();
    order.status = status.name();
    order.totalCostCents = 0L;
    order.pickupDate = pickupDate.toDate();
    order.deliveryDate = deliveryDate.toDate();
    Days days = Days.daysBetween(pickupDate, deliveryDate);
    order.rushService = days == Days.ONE || days == Days.ZERO;

    try {
      session.save(order);
    } catch (ConstraintViolationException e) {
      logger.warn("Error creating [order=%s] for [user=%s]", orderType, user.getId(), e);
      throw e;
    }

    return order;
  }
}
