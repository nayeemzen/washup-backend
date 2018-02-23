package com.washup.app.orders;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.Id;
import com.washup.protos.App.ReceiptItem;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;

public class ItemizedReceiptOperator {
  private final Session session;
  private final List<DbReceiptItem> itemReceipts;

  public ItemizedReceiptOperator(Session session, List<DbReceiptItem> itemReceipts) {
    this.session = session;
    this.itemReceipts = itemReceipts;
  }

  public List<ReceiptItem> toProto() {
    return itemReceipts.stream()
        .map(DbReceiptItem::toProto)
        .collect(Collectors.toList());
  }

  @Component
  public static class Factory {
    public ItemizedReceiptOperator get(Session session, Id<DbOrder> orderId) {
      List<DbReceiptItem> receiptItems = session.createCriteria(DbReceiptItem.class)
          .add(eq("orderId", orderId.getId()))
          .addOrder(Order.asc("id"))
          .list();
      return receiptItems.isEmpty() ? null : new ItemizedReceiptOperator(session, receiptItems);
    }
  }
}
