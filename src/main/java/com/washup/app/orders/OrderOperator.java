package com.washup.app.orders;

import com.washup.app.database.hibernate.Id;
import com.washup.app.users.DbUser;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import static org.hibernate.criterion.Restrictions.eq;

public class OrderOperator {

  private final Session session;
  private final DbOrder order;

  public OrderOperator(Session session, DbOrder order) {
    this.session = session;
    this.order = order;
  }

  @Component
  public static class Factory {

    public OrderOperator create(Session session, Id<DbUser> userId, String orderType,
        String idempotenceToken, String status, long deliveryDate,
        long pickupDate) {
      DateTime deliveryDateUtc = new DateTime(deliveryDate, DateTimeZone.UTC);
      DateTime pickupDateUtc = new DateTime(pickupDate, DateTimeZone.UTC);
      DbOrder order = DbOrder.create(session, userId, idempotenceToken,
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
