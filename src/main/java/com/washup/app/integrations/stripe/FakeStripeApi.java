package com.washup.app.integrations.stripe;

import com.stripe.model.Customer;
import com.washup.app.orders.OrderToken;

public class FakeStripeApi implements StripeApi {
  @Override
  public Customer createCustomer(String email, String cardToken) throws Exception {
    return null;
  }

  @Override
  public Customer updateCustomer(String stripeCustomerToken, String stripeCardToken)
      throws Exception {
    return null;
  }

  @Override
  public String chargeCustomer(String stripeCustomerToken, OrderToken orderToken, long amountCents)
      throws Exception {
    return null;
  }
}
