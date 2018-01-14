package com.washup.app.api.v1.pricing;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.BadRequestException;
import com.washup.protos.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.washup.app.api.v1.ApiConstants.API_URL;

@RestController
@RequestMapping(PricingController.URL)
public class PricingController {
  static final String URL = API_URL + "/users";

  @Autowired
  Transacter transacter;

  @RequestMapping("/prices")
  public App.Prices prices() {
    throw new BadRequestException("a");
  }
}
