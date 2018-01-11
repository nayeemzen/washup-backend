package com.washup.app.api.v1.pricing;

import com.google.common.collect.ImmutableList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PricingController {
    private final static PricingResponse PRICES = new PricingResponse(ImmutableList.of(
            new PricingResponse.CategoryPricing("Dry Cleaning",
                    ImmutableList.of(new PricingResponse.ItemPrice("Pressed Shirt", 295L))),
            new PricingResponse.CategoryPricing("Wash & Fold",
                    ImmutableList.of(new PricingResponse.ItemPrice("Per Pound", 195L)))
    ));

    @RequestMapping("/prices")
    public PricingResponse prices() {
        return PRICES;
    }
}
