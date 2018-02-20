package com.washup.app.pricing;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.AbstractOperator;
import com.washup.app.database.hibernate.Id;
import com.washup.app.pricing.DbPostalCode.Rules;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class PostalCodeOperator extends AbstractOperator<DbPostalCode> {

  private final ItemPricingFetcher.Factory itemPricingFetcherFactory;

  public PostalCodeOperator(Session session, DbPostalCode postalCode,
      ItemPricingFetcher.Factory itemPricingFetcherFactory) {
    super(session, postalCode);
    this.itemPricingFetcherFactory = itemPricingFetcherFactory;
  }

  public Id<DbPostalCode> getId() {
    return entity.getId();
  }

  public ItemPricingFetcher getPricingFetcher() {
    return itemPricingFetcherFactory.get(session, entity.getBucketId());
  }

  @Component
  public static class Factory {

    @Autowired ItemPricingFetcher.Factory itemPricingFetcherFactory;

    public @Nullable PostalCodeOperator get(Session session, String postalCode) {
      String normalizedPostalCode = postalCode.replace("\\s+", "");
      DbPostalCode dbPostalCode = (DbPostalCode) session.createCriteria(DbPostalCode.class)
          .add(eq("postalCode", normalizedPostalCode))
          .add(eq("rule", Rules.FULL_MATCH.name()))
          .uniqueResult();
      // If full match was found, return it.
      if (dbPostalCode != null) {
        return new PostalCodeOperator(session, dbPostalCode, itemPricingFetcherFactory);
      }

      // Do suffix match on first two characters and then first three characters
      for (int i = 0; i < 2; i++) {
        if (normalizedPostalCode.length() < i + 2) {
          return null;
        }
        String substring = normalizedPostalCode.substring(0, i + 2);
        dbPostalCode = (DbPostalCode) session.createCriteria(DbPostalCode.class)
            .add(eq("postalCode", substring))
            .add(eq("rule", Rules.STARTS_WITH.name()))
            .uniqueResult();
        if (dbPostalCode != null) {
          return new PostalCodeOperator(session, dbPostalCode, itemPricingFetcherFactory);
        }
      }

      return null;
    }
  }
}
