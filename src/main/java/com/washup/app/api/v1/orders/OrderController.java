package com.washup.app.api.v1.orders;

import static com.washup.app.api.v1.ApiConstants.API_URL;
import static java.util.stream.Collectors.toList;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.or;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.orders.DbOrder;
import com.washup.app.orders.OrderOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import com.washup.protos.App.GetOrdersRequest;
import com.washup.protos.App.GetOrdersResponse;
import com.washup.protos.App.PlaceOrderResponse;
import com.washup.protos.Shared;
import com.washup.protos.Shared.Order;
import java.util.Collections;
import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
  OrderQuery.Factory orderQueryFactory;

  @Autowired
  UserOperator.Factory userOperatorFactory;

  @PostMapping("/place-order")
  public PlaceOrderResponse placeOrder(@RequestBody App.PlaceOrderRequest request,
      Authentication authentication) {
    ParametersChecker.check(request.getOrderType() != null,
        "order_type is missing");
    ParametersChecker.check(request.getDeliveryDate() != 0,
        "delivery_date is missing");
    ParametersChecker.check(request.getPickupDate() != 0,
        "pickup_date is missing");

    transacter.execute(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      orderOperatorFactory.create(session,
          user,
          request.getOrderType().name(),
          request.getIdempotenceToken(),
          Shared.OrderStatus.PENDING.name(),
          request.getDeliveryDate(),
          request.getPickupDate());
    });

    return PlaceOrderResponse.newBuilder().build();
  }

  @GetMapping("/get-orders")
  public GetOrdersResponse getOrders(Authentication authentication) {
    List<Order> orders = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      List<OrderOperator> orderOperators = orderQueryFactory.get(session)
          .userId(user.getId())
          .list();
      return orderOperators.stream().map(OrderOperator::toWire).collect(toList());
    });

    return GetOrdersResponse.newBuilder().addAllOrders(orders).build();
  }
}
