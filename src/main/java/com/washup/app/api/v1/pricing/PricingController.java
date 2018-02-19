package com.washup.app.api.v1.pricing;

import static com.washup.app.api.v1.ApiConstants.API_URL;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.pricing.ItemPricingFetcher;
import com.washup.app.pricing.PostalCodeOperator;
import com.washup.protos.App.GetPostalCodePricingRequest;
import com.washup.protos.App.GetPostalCodePricingResponse;
import com.washup.protos.App.GetUserPricingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PricingController.URL)
public class PricingController {

  static final String URL = API_URL + "/pricing";

  @Autowired
  Transacter transacter;

  @Autowired
  PostalCodeOperator.Factory postalCodeOperatorFactory;

  @GetMapping("/get-user-pricing")
  public GetUserPricingResponse getUserPricing(Authentication authentication) {
    throw new UnsupportedOperationException();
  }

  @PostMapping("/get-postal-code-pricing")
  public GetPostalCodePricingResponse getPostalCodePricing(
      @RequestBody GetPostalCodePricingRequest request) {
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getPostalCode()),
        "postal_code is required");
    return transacter.call(session -> {
      PostalCodeOperator postalCodeOperator = postalCodeOperatorFactory
          .get(session, request.getPostalCode());
      // No postal code match was found.
      if (postalCodeOperator == null) {
        return GetPostalCodePricingResponse.newBuilder()
            .build();
      }
      ItemPricingFetcher pricingFetcher = postalCodeOperator.getPricingFetcher();
      return GetPostalCodePricingResponse.newBuilder()
          .addAllDryClean(pricingFetcher.dryCleanPricing())
          .addAllWashFold(pricingFetcher.washFoldPricing())
          .build();
    });
  }
}
