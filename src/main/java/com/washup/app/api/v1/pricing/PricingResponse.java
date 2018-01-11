package com.washup.app.api.v1.pricing;

import java.util.List;

public class PricingResponse {
    private final List<CategoryPricing> categoryPricings;

    public PricingResponse(List<CategoryPricing> categoryPricings) {
        this.categoryPricings = categoryPricings;
    }

    public List<CategoryPricing> getCategoryPricings() {
        return categoryPricings;
    }

    public static class ItemPrice {
        private final String item;
        private final Long amountCents;

        public ItemPrice(String item, Long amountCents) {
            this.item = item;
            this.amountCents = amountCents;
        }

        public String getItem() {s
            return item;
        }

        public Long getAmountCents() {
            return amountCents;
        }
    }

    public static class CategoryPricing {
        private final String category;
        private final List<ItemPrice> prices;

        public CategoryPricing(String category, List<ItemPrice> prices) {
            this.category = category;
            this.prices = prices;
        }

        public String getCategory() {
            return category;
        }

        public List<ItemPrice> getPrices() {
            return prices;
        }
    }
}
