package com.washup.app.api.v1.users;

import static com.washup.app.api.v1.ApiConstants.API_URL;

import com.google.common.base.Strings;
import com.stripe.exception.CardException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.integrations.stripe.StripeApi;
import com.washup.app.users.DbUser;
import com.washup.app.users.PaymentCardOperator;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import com.washup.protos.App.GetPaymentCardResponse;
import com.washup.protos.App.PaymentCard;
import com.washup.protos.App.SetPaymentCardResponse;
import com.washup.protos.App.SetPaymentCardResponse.Status;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PaymentCardController.URL)
public class PaymentCardController {
  private final static Logger logger = LoggerFactory.getLogger(PaymentCardController.class);
  static final String URL = API_URL + "/users";

  @Autowired
  Transacter transacter;

  @Autowired
  PaymentCardOperator.Factory paymentCardFactory;

  @Autowired
  UserOperator.Factory userOperatorFactory;

  @Autowired
  StripeApi stripeApi;

  @PostMapping("/set-card")
  public App.SetPaymentCardResponse SetCard(@RequestBody App.SetPaymentCardRequest request,
      Authentication authentication) {
    String stripeCardToken = request.getStripeCardToken();
    ParametersChecker.check(!Strings.isNullOrEmpty(stripeCardToken), "Card token is required");

    CustomerCardData customerCardData = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      PaymentCardOperator paymentCardOperator = paymentCardFactory.get(session, user.getId());
      String customerStripeToken = paymentCardOperator != null
          ? paymentCardOperator.getStripeCustomerToken()
          : null;
      return new CustomerCardData(user.getId(), user.getEmail(), customerStripeToken);
    });

    // Lets call stripe and create a customer profile with card information
    try {
      if (customerCardData.customerStripeToken != null) {
        Customer customer = stripeApi.updateCustomer(customerCardData.customerStripeToken,
            stripeCardToken);
        customerCardData.setLastFour(customer);
      } else {
        // Store the customerStripeToken
        Customer customer = stripeApi.createCustomer(customerCardData.email, stripeCardToken);
        customerCardData.customerStripeToken = customer.getId();
        customerCardData.setLastFour(customer);
      }
    } catch (CardException e) {
      logger.warn("Unable to link card [userId=%s][token=%s]", customerCardData.userId,
          stripeCardToken, e);
      return SetPaymentCardResponse.newBuilder()
          .setStatus(Status.FAILURE)
          .build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    transacter.execute(session -> {
      PaymentCardOperator paymentCardOperator = paymentCardFactory
          .get(session, customerCardData.userId);
      if (paymentCardOperator == null) {
        paymentCardFactory.create(session, customerCardData.userId,
            customerCardData.customerStripeToken, customerCardData.lastFour);
      } else {
        paymentCardOperator.setLastFour(customerCardData.lastFour)
            .update();
      }
    });

    return SetPaymentCardResponse.newBuilder()
        .setStatus(Status.SUCCESS)
        .build();
  }

  @GetMapping("/get-card")
  public App.GetPaymentCardResponse getCard(Authentication authentication) {
    return transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session,
          authentication);
      PaymentCardOperator paymentCardOperator = paymentCardFactory.get(session, user.getId());
      return paymentCardOperator != null
          ? GetPaymentCardResponse.newBuilder()
              .setCard(PaymentCard.newBuilder()
                  .setLastFour(paymentCardOperator.getLastFour())
                  .build())
              .build()
          : GetPaymentCardResponse.newBuilder()
              .build();
    });
  }

  private final class CustomerCardData {
    public final Id<DbUser> userId;
    public final String email;
    public @Nullable String customerStripeToken;
    public @Nullable String lastFour;

    public CustomerCardData(Id<DbUser> userId, String email, @Nullable String customerStripeToken) {
      this.userId = userId;
      this.email = email;
      this.customerStripeToken = customerStripeToken;
      this.lastFour = null;
    }

    public void setLastFour(Customer customer) {
      if (customer.getSources() == null
          || customer.getSources().getData() == null
          || customer.getSources().getData().isEmpty()) {
        return;
      }
      Card card = (Card) customer.getSources().getData().get(0);
      this.lastFour = card.getLast4();
    }
  }
}
