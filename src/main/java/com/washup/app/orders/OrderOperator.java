package com.washup.app.orders;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.AbstractOperator;
import com.washup.app.database.hibernate.Id;
import com.washup.app.users.DbUser;
import com.washup.app.users.UserOperator;
import com.washup.protos.Admin.OrderAdmin;
import com.washup.protos.App.Order;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

public class OrderOperator extends AbstractOperator<DbOrder> {

  public OrderOperator(Session session, DbOrder order) {
    super(session, order);
  }

  public Id<DbOrder> getId() {
    return entity.getId();
  }

  public OrderAdmin toInternal() {
    return entity.toInternal();
  }

  public Order toProto() {
    return entity.toProto();
  }

  public DbUser getUser() {
    return entity.getUser();
  }

  @Component
  public static class Factory {

    public OrderOperator create(Session session, UserOperator user, OrderType orderType,
        String idempotenceToken, OrderStatus status, long deliveryDate,
        long pickupDate) {
      DateTime deliveryDateUtc = new DateTime(deliveryDate, DateTimeZone.UTC);
      DateTime pickupDateUtc = new DateTime(pickupDate, DateTimeZone.UTC);
      DbOrder order = DbOrder.create(session, user, idempotenceToken,
          orderType, status, deliveryDateUtc, pickupDateUtc);
      return new OrderOperator(session, order);
    }

    public OrderOperator get(Session session, OrderToken orderToken) {
      DbOrder order = (DbOrder) session.createCriteria(DbOrder.class)
          .add(eq("token", orderToken.getId()))
          .uniqueResult();
      return order != null ? new OrderOperator(session, order) : null;
    }
  }
}
