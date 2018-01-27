package com.washup.app.orders;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.or;

import com.washup.app.database.hibernate.AbstractOperator;
import com.washup.app.database.hibernate.Id;
import com.washup.app.users.DbUser;
import com.washup.app.users.UserOperator;
import com.washup.protos.Shared;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class OrderOperator extends AbstractOperator<DbOrder> {

  public OrderOperator(Session session, DbOrder order) {
    super(session, order);
  }

  public Shared.Order toWire() {
    return entity.toWire();
  }

  @Component
  public static class Factory {

    @Autowired OrderQuery.Factory orderQueryFactory;

    public OrderOperator create(Session session, UserOperator user, String orderType,
        String idempotenceToken, String status, long deliveryDate,
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
