package com.washup.app.api.v1.orders;

import static com.washup.app.api.v1.ApiConstants.API_URL;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.orders.OrderOperator;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import com.washup.protos.Shared;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(OrderController.URL)
public class OrderController {

  static final String URL = API_URL + "/orders";

  @Autowired
  Transacter transacter;

  @Autowired
  OrderOperator.Factory orderOperatorFactory;

  @Autowired
  UserOperator.Factory userOperatorFactory;

  @PostMapping("/place-order")
  public App.PlaceOrderResponse placeOrder(
      @RequestBody App.PlaceOrderRequest request,
      Authentication authentication) {
    ParametersChecker.check(request.getOrderType() != null,
        "order_type is missing");
    ParametersChecker.check(request.getDeliveryDate() != 0,
        "delivery_date is missing");
    ParametersChecker.check(request.getPickupDate() != 0,
        "pickup_date is missing");

    transacter.execute(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session,
          authentication);
      orderOperatorFactory.create(session,
          user,
          request.getOrderType().name(),
          request.getIdempotenceToken(),
          Shared.OrderStatus.PENDING.name(),
          request.getDeliveryDate(),
          request.getPickupDate());
    });

    return App.PlaceOrderResponse.newBuilder().build();
  }
}
