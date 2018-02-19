package com.washup.app.api.v1.pricing;

import static com.washup.app.api.v1.ApiConstants.API_URL;

import com.washup.app.database.hibernate.Transacter;
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

  @GetMapping("/get-user-pricing")
  public GetUserPricingResponse getUserPricing(Authentication authentication) {
    throw new UnsupportedOperationException();
  }

  @PostMapping("/get-user-pricing")
  public GetPostalCodePricingResponse getPostalCodePricing(
      @RequestBody GetPostalCodePricingRequest request, Authentication authentication) {
    throw new UnsupportedOperationException();
  }
}
