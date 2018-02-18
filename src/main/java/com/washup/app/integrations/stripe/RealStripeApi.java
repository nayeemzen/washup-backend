package com.washup.app.integrations.stripe;

import static com.google.common.base.Preconditions.checkNotNull;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.washup.app.orders.OrderToken;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

public class RealStripeApi implements StripeApi {
  private static final Logger logger = LoggerFactory.getLogger(RealStripeApi.class);
  private static final String SECRET_API_KEY_NAME = "STRIPE_SECRET_API_KEY";

  public RealStripeApi(Environment environment) {
    Stripe.apiKey = checkNotNull(environment.getProperty(SECRET_API_KEY_NAME));
  }

  @Override
  public Customer createCustomer(String email, String stripeCardToken)
      throws Exception {
    Map<String, Object> customerParams = new HashMap<>();
    customerParams.put("email", checkNotNull(email));
    customerParams.put("source", stripeCardToken);
    Customer customer = Customer.create(customerParams);
    return customer;
  }

  @Override
  public Customer updateCustomer(String stripeCustomerToken, String stripeCardToken)
      throws Exception {
    Customer customer = Customer.retrieve(stripeCustomerToken);
    Map<String, Object> customerParams = new HashMap<>();
    customerParams.put("source", stripeCardToken);
    customer.update(customerParams);
    return customer;
  }

  @Override
  public String chargeCustomer(String stripeCustomerToken, OrderToken orderToken, long amountCents)
      throws Exception {
    Map<String, Object> chargeParams = new HashMap<>();
    chargeParams.put("amount", amountCents);
    chargeParams.put("currency", "cad");
    chargeParams.put("capture", "Charge for Order: " + orderToken.toString());
    chargeParams.put("capture", "washup.io " + orderToken.toString());
    chargeParams.put("customer", stripeCustomerToken);
    return Charge.create(chargeParams).getId();
  }
}
