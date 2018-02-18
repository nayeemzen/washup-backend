package com.washup.app.integrations.stripe;

import com.google.common.collect.ImmutableList;
import com.stripe.model.Customer;
import com.washup.app.orders.OrderToken;
import java.util.List;

public interface StripeApi {
  List<String> CARD_ERROR_CODES = ImmutableList.of(
      "invalid_number",
      "invalid_expiry_month",
      "invalid_expiry_year",
      "invalid_cvc",
      "invalid_swipe_data",
      "incorrect_number",
      "expired_card",
      "incorrect_cvc",
      "incorrect_zip",
      "card_declined",
      "missing",
      "processing_error");


  /**
   * Creates Stripe customer profile with the card and billing information. Raises exception in
   * case of failure.
   * @param email
   * @param cardToken
   * @return
   * @throws Exception
   */
  Customer createCustomer(String email, String cardToken)
      throws Exception;

  /**
   * Update card and billing information for the customer.
   * @param stripeCustomerToken
   * @param stripeCardToken
   * @throws Exception
   */
  Customer updateCustomer(String stripeCustomerToken, String stripeCardToken)
      throws Exception;

  /**
   * Charges customer's card on file with the amount for the provided order.
   * @param stripeCustomerToken
   * @param orderToken
   * @param amountCents
   * @return
   * @throws Exception
   */
  String chargeCustomer(String stripeCustomerToken, OrderToken orderToken, long amountCents)
      throws Exception;

  enum CardErrorCode {
    InvalidNumber("invalid_number"),
    InvalidExpiryMonth("invalid_expiry_month"),
    InvalidExpiryYear("invalid_expiry_year"),
    InvalidCvc("invalid_cvc"),
    InvalidSwipeData("invalid_swipe_data"),
    IncorrectNumber("incorrect_number"),
    ExpiredCard("expired_card"),
    IncorrectCvc("incorrect_cvc"),
    CardDeclined("card_declined"),
    Missing("missing"),
    ProcessingError("processing_error");


    public final String description;

    CardErrorCode(String description) {
      this.description = description;
    }
  }
}
