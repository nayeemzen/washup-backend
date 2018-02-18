package com.washup.app.users;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.IdEntity;
import com.washup.app.database.hibernate.StoreAsString;
import com.washup.app.database.hibernate.TimestampEntity;
import com.washup.protos.App.PaymentCard;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity(name = "payment_cards")
@Table(name = "payment_cards")
public class DbPaymentCard extends TimestampEntity implements IdEntity {
  private final static Logger logger = LoggerFactory.getLogger(DbPaymentCard.class);

  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @StoreAsString
  private String token;

  @Column(updatable = false)
  private Long userId;

  private String stripeCustomerToken;

  private String lastFour;

  @Override
  public Id<DbPaymentCard> getId() {
    return new Id<>(id);
  }

  public CardToken getToken() {
    return new CardToken(token);
  }

  public Id<DbUser> getUserId() {
    return new Id<>(userId);
  }

  public String getStripCustomerToken() {
    return stripeCustomerToken;
  }

  public String getLastFour() {
    return lastFour;
  }

  public void setLastFour(String lastFour) {
    this.lastFour = lastFour;
  }

  public PaymentCard toProto() {
    return PaymentCard.newBuilder()
        .setLastFour(lastFour)
        .build();
  }

  static @Nullable
  DbPaymentCard get(Session session, Id<DbUser> userId) {
    return (DbPaymentCard) session.createCriteria(DbPaymentCard.class)
        .add(eq("userId", userId.getId()))
        .uniqueResult();
  }

  static DbPaymentCard create(Session session, Id<DbUser> userId, String stripeCustomerToken,
      String lastFour) {
    DbPaymentCard dbCard = new DbPaymentCard();
    dbCard.token = CardToken.generate().getId();
    dbCard.userId = userId.getId();
    dbCard.stripeCustomerToken = stripeCustomerToken;
    dbCard.lastFour = lastFour;

    try {
      session.save(dbCard);
    } catch (ConstraintViolationException e) {
      logger.warn("Failed saving card for [userId=%s]", userId);
      throw e;
    }

    return dbCard;
  }
}
