package com.washup.app.users;

import com.washup.app.database.hibernate.Id;
import com.washup.protos.App.PaymentCard;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

public class PaymentCardOperator {

  private final Session session;
  private final DbPaymentCard card;

  public PaymentCardOperator(Session session, DbPaymentCard address) {
    this.session = session;
    this.card = address;
  }

  public Id<DbPaymentCard> getId() {
    return card.getId();
  }

  public String getStripeCustomerToken() {
    return card.getStripCustomerToken();
  }

  public @Nullable String getLastFour() {
    return this.card.getLastFour();
  }

  public PaymentCardOperator setLastFour(String lastFour) {
    this.card.setLastFour(lastFour);
    return this;
  }

  public PaymentCardOperator update() {
    session.update(card);
    return this;
  }

  public PaymentCard toProto() {
    return this.card.toProto();
  }

  @Component
  public static class Factory {
    public @Nullable
    PaymentCardOperator get(Session session, Id<DbUser> userId) {
      DbPaymentCard card = DbPaymentCard.get(session, userId);
      return card != null
          ? new PaymentCardOperator(session, card)
          : null;
    }

    public PaymentCardOperator create(Session session, Id<DbUser> userId,
        String stripeCustomerToken, String lastFour) {
      DbPaymentCard card = DbPaymentCard.create(session, userId, stripeCustomerToken, lastFour);
      return new PaymentCardOperator(session, card);
    }
  }
}
