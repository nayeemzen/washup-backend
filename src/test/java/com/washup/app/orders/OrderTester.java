package com.washup.app.orders;

import static com.google.common.base.Preconditions.checkNotNull;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.Transacter;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.assertj.core.api.DateAssert;
import org.assertj.core.api.ObjectAssert;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class OrderTester {

  private final Transacter transacter;
  private final Id<DbOrder> lastOrderId;

  public OrderTester(Factory factory, Id<DbOrder> lastOrderId) {
    this.transacter = factory.transacter;
    this.lastOrderId = checkNotNull(lastOrderId);
  }

  public ObjectAssert<OrderType> assertOrderType() {
    return transacter.call(session -> new ObjectAssert<>(load(session).getOrderType()));
  }

  public ObjectAssert<OrderStatus> assertStatus() {
    return transacter.call(session -> new ObjectAssert<>(load(session).getStatus()));
  }

  public DateAssert assertPickupDate() {
    return transacter.call(session -> new DateAssert(load(session).getPickupDate().toDate()));
  }

  public DateAssert assertDeliveryDate() {
    return transacter.call(session -> new DateAssert(load(session).getDeliveryDate().toDate()));
  }

  public ObjectAssert<Boolean> assertRushService() {
    return transacter.call(session -> new ObjectAssert<>(load(session).isRushService()));
  }

  public ObjectAssert<Long> assertTotalCostCents() {
    return transacter.call(session -> new ObjectAssert<>(load(session).getTotalCostCents()));
  }

  public OrderToken getOrderToken() {
    return transacter.call(session -> load(session).getToken());
  }

  public Date getCreatedAt() {
    return transacter.call(session -> load(session).getCreatedAt());
  }

  public Date getUpdateAt() {
    return transacter.call(session -> load(session).getUpdatedAt());
  }

  private DbOrder load(Session session) {
    return session.load(DbOrder.class, lastOrderId.getId());
  }

  @Component
  public static class Factory {
    @Autowired
    Transacter transacter;

    @Autowired
    OrderQuery.Factory orderQueryFactory;

    public @Nullable OrderTester last() {
      Id<DbOrder> lastOrderId = transacter.call(session -> {
        List<OrderOperator> orders = orderQueryFactory.get(session)
            .orderDesc("id")
            .list();
        return !orders.isEmpty() ? orders.get(0).getId() : null;
      });
      return lastOrderId != null ? new OrderTester(this, lastOrderId) : null;
    }

    public List<OrderTester> all() {
      return transacter.call(session -> orderQueryFactory.get(session)
            .orderDesc("id")
            .list()
            .stream()
            .map(m -> new OrderTester(this, m.getId()))
            .collect(Collectors.toList()));
    }
  }
}
