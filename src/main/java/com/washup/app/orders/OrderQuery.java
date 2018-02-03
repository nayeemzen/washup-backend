package com.washup.app.orders;

import com.washup.app.database.hibernate.AbstractQuery;
import com.washup.app.database.hibernate.Id;
import com.washup.app.users.DbUser;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

public class OrderQuery extends AbstractQuery<DbOrder, OrderQuery, OrderOperator> {
  private OrderQuery(Session session) {
    super(session, DbOrder.class, OrderOperator.class);
  }

  public OrderQuery userId(Id<DbUser> userId) {
    criteria.add(Restrictions.eq("userId", userId.getId()));
    return this;
  }

  public OrderQuery orderType(OrderType type) {
    criteria.add(Restrictions.eq("orderType", type.name()));
    return this;
  }

  public OrderQuery orderStatus(OrderStatus orderStatus) {
    criteria.add(Restrictions.eq("status", orderStatus.name()));
    return this;
  }

  public OrderQuery isBilled(Boolean billed) {
    if (billed) {
      criteria.add(Restrictions.isNotNull("billedAt"));
    }
    return this;
  }

  public OrderQuery ordersBetween(DateTime start, DateTime end) {
    criteria.add(Restrictions.or(
        Restrictions.between("pickupDate", start.toDate(), end.toDate()),
        Restrictions.between("deliveryDate", start.toDate(), end.toDate())));
    return this;
  }

  public OrderQuery droppedOffBetween(DateTime start, DateTime end) {
    criteria.add(Restrictions.between("pickupDate", start.toDate(), end.toDate()));
    return this;
  }

  public OrderQuery deliveryBetween(DateTime start, DateTime end) {
    criteria.add(Restrictions.between("deliveryDate", start.toDate(), end.toDate()));
    return this;
  }

  @Component
  public static class Factory {
    public OrderQuery get(Session session) {
      return new OrderQuery(session);
    }
  }
}
