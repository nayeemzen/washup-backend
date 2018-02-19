package com.washup.app.pricing;

import static org.hibernate.criterion.Restrictions.eq;

import com.washup.app.database.hibernate.Id;
import com.washup.protos.App.Pricing;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

public class ItemPricingFetcher {
  private final Session session;
  private final List<DbItemPricing> itemPricings;

  public ItemPricingFetcher(Session session, List<DbItemPricing> itemPricings) {
    this.session = session;
    this.itemPricings = itemPricings;
  }

  public List<Pricing> dryCleanPricing() {
    return getPricing(CleaningItems.DRY_CLEAN);
  }

  public List<Pricing> washFoldPricing() {
    return getPricing(CleaningItems.WASH_FOLD);
  }


  private List<Pricing> getPricing(Set<CleaningItems.Item> category) {
    return itemPricings.stream()
        .filter(i -> category.contains(CleaningItems.Item.valueOf(i.getItem())))
        .map(m -> m.toProto())
        .collect(Collectors.toList());
  }

  @Component
  public static class Factory {
    public @Nullable ItemPricingFetcher get(Session session, Id<DbPricingBucket> pricingBucketId) {
      List<DbItemPricing> itemPricings = session.createCriteria(DbItemPricing.class)
          .add(eq("bucketId", pricingBucketId.getId()))
          .list();
      return new ItemPricingFetcher(session, itemPricings);
    }
  }
}
