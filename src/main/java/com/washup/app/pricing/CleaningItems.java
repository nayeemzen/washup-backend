package com.washup.app.pricing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class CleaningItems {
  enum DryClean {
    LAUNDERED_SHIRT,
    DRY_CLEAN_SHIRT,
  }

  enum WashFold {
    PER_POUND,
    DOUBLE_BLANKET,
  }

  enum Item {
    // Dry Clean
    LAUNDERED_SHIRT("Laundered Shirt"),
    DRY_CLEAN_SHIRT("Dry Clean Shirt"),

    // Wash & Fold
    PER_POUND("Per Pound"),
    DOUBLE_BLANKET("Double Blanket");

    public String canonicalName;

    Item(String canonicalName) {
      this.canonicalName = canonicalName;
    }
  }

  public final static Set<Item> DRY_CLEAN = ImmutableSet.of(Item.LAUNDERED_SHIRT,
      Item.DRY_CLEAN_SHIRT);

  public final static Set<Item> WASH_FOLD = ImmutableSet.of(Item.PER_POUND, Item.DOUBLE_BLANKET);
}
