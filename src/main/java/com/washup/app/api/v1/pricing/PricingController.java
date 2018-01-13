package com.washup.app.api.v1.pricing;

import com.washup.app.database.hibernate.Transacter;
import com.washup.protos.App;
import io.jsonwebtoken.JwtBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PricingController {

  @Autowired
  Transacter transacter;

  @RequestMapping("/prices")
  public App.Prices prices() {
    return App.Prices.newBuilder().setAbc("oooo").build();
  }
}
