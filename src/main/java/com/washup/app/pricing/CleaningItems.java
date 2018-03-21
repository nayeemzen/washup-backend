package com.washup.app.pricing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class CleaningItems {
  /**
   * Laundered & Pressed Shirt	3.29
   Dry Cleaned Shirt	7.99
   Blouse	7.99
   Tie	5.99
   Skirt	8.99
   Pants	8.99
   Vest	8.99
   Sweater	9.95
   Suit Jacket	13.99
   Dress	15.99
   Suit 2 Pc	22.99
   Coat	21.99
   Duvet / Comforter	39.99
   */

  enum Item {
    // Dry Clean
    LAUNDERED_SHIRT("Laundered Shirt"),
    DRY_CLEAN_SHIRT("Dry Clean Shirt"),
    BLOUSE("Bloue"),
    TIE("Tie"),
    SKIRT("Skirt"),
    PANTS("Pants"),
    VEST("Vest"),
    SWEATER("Sweater"),
    SUIT_JACKET("Suit Jacket"),
    DRESS("Dress"),
    SUIT_2_PC("Suit 2 Pc"),
    COAT("Coat"),
    DUVET("Duvet / Comforter"),

    // Wash & Fold
    PER_POUND("Per Pound"),
    DOUBLE_BLANKET("Double/Twin Blanket or Comforter"),
    QUEEN_COMFORTER("Queen Blanket or Comforter"),
    KING_COMFORTER("King Blanket or Comforter"),
    BED_SPREAD("Bed Spread or Sheets");

    public String canonicalName;

    Item(String canonicalName) {
      this.canonicalName = canonicalName;
    }
  }

  public final static Set<Item> DRY_CLEAN = ImmutableSet.of(
      Item.LAUNDERED_SHIRT,
      Item.DRY_CLEAN_SHIRT,
      Item.BLOUSE,
      Item.TIE,
      Item.SKIRT,
      Item.PANTS,
      Item.VEST,
      Item.SWEATER,
      Item.SUIT_JACKET,
      Item.DRESS,
      Item.SUIT_2_PC,
      Item.COAT,
      Item.DUVET);

  public final static Set<Item> WASH_FOLD = ImmutableSet.of(
      Item.PER_POUND,
      Item.DOUBLE_BLANKET,
      Item.QUEEN_COMFORTER,
      Item.KING_COMFORTER,
      Item.BED_SPREAD);
}
