package com.washup.app.api.v1.orders;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.api.v1.ApiConstants.API_URL;
import static java.util.stream.Collectors.toList;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.orders.ItemizedReceiptOperator;
import com.washup.app.orders.OrderOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.app.orders.OrderToken;
import com.washup.app.pricing.PostalCodeOperator;
import com.washup.app.users.AddressOperator;
import com.washup.app.users.PaymentCardOperator;
import com.washup.app.users.UserOperator;
import com.washup.protos.App;
import com.washup.protos.App.GetOrdersRequest;
import com.washup.protos.App.GetOrdersResponse;
import com.washup.protos.App.GetReceiptRequest;
import com.washup.protos.App.GetReceiptResponse;
import com.washup.protos.App.Order;
import com.washup.protos.App.PlaceOrderResponse;
import com.washup.protos.App.Receipt;
import com.washup.protos.App.ReceiptItem;
import com.washup.protos.App.ServiceAvailability;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import java.util.List;
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

  @Autowired
  AddressOperator.Factory addressOperatorFactory;

  @Autowired
  PaymentCardOperator.Factory paymentCardOperatorFactory;

  @Autowired
  PostalCodeOperator.Factory postalCodeOperatorFactory;

  @Autowired
  ItemizedReceiptOperator.Factory itemizedReceiptOperatorFactory;

  @PostMapping("/place-order")
  public PlaceOrderResponse placeOrder(@RequestBody App.PlaceOrderRequest request,
      Authentication authentication) {
    ParametersChecker.check(request.getOrderType() != null,
        "order_type is missing");
    ParametersChecker.check(request.getDeliveryDate() != 0,
        "delivery_date is missing");
    ParametersChecker.check(request.getPickupDate() != 0,
        "pickup_date is missing");
    ParametersChecker.check(request.getIdempotenceToken() != null, "idempotence_token is missing");

    return transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      PaymentCardOperator paymentCardOperator = paymentCardOperatorFactory
          .get(session, user.getId());
      ParametersChecker.check(paymentCardOperator != null,
          "user must set payment card before ordering");
      AddressOperator addressOperator = addressOperatorFactory.get(session, user.getId());
      ParametersChecker.check(addressOperator != null, "user must set address before ordering");
      PostalCodeOperator postalCodeOperator = postalCodeOperatorFactory
          .get(session, addressOperator.getPostalCode());
      ParametersChecker.check(postalCodeOperator != null,
          "Service is not available in your region");
      ServiceAvailability availibilty = postalCodeOperator.getAvailibilty();
      if (request.getOrderType() == OrderType.DRY_CLEAN) {
        ParametersChecker.check(availibilty == ServiceAvailability.WASH_FOLD_DRY_CLEANING_AVAILABLE
            || availibilty == ServiceAvailability.ONLY_DRY_CLEANING_AVAILABLE,
            "Dry cleaning is not available in your area");
      }
      if (request.getOrderType() == OrderType.WASH_FOLD) {
        ParametersChecker.check(availibilty == ServiceAvailability.WASH_FOLD_DRY_CLEANING_AVAILABLE
                || availibilty == ServiceAvailability.ONLY_WASH_FOLD_AVAILABLE,
            "Wash Fold is not available in your area");
      }
      OrderOperator orderOperator = orderOperatorFactory.create(session,
          user,
          request.getOrderType(),
          request.getIdempotenceToken(),
          OrderStatus.PENDING,
          request.getDeliveryDate(),
          request.getPickupDate());
      return PlaceOrderResponse.newBuilder()
          .setOrder(orderOperator.toProto())
          .build();
    });
  }

  @GetMapping("/get-orders")
  public GetOrdersResponse getOrders(GetOrdersRequest request, Authentication authentication) {
    List<Order> orders = transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      List<OrderOperator> orderOperators = orderQueryFactory.get(session)
          .userId(user.getId())
          .orderDesc("id")
          .list();
      return orderOperators.stream().map(OrderOperator::toProto).collect(toList());
    });

    return GetOrdersResponse.newBuilder().addAllOrders(orders).build();
  }

  @PostMapping("/get-receipt")
  public GetReceiptResponse getReceipt(@RequestBody GetReceiptRequest request,
      Authentication authentication) {
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getOrderToken()),
        "order_token is missing");
    return transacter.call(session -> {
      UserOperator user = userOperatorFactory.getAuthenticatedUser(session, authentication);
      OrderOperator orderOperator = orderOperatorFactory
          .get(session, new OrderToken(request.getOrderToken()));
      checkState(user.getId().equals(orderOperator.getUser().getId()));
      ItemizedReceiptOperator itemizedReceiptOperator = itemizedReceiptOperatorFactory
          .get(session, orderOperator.getId());
      if (itemizedReceiptOperator == null) {
        return GetReceiptResponse.newBuilder()
            .build();
      }
      List<ReceiptItem> receiptItems = itemizedReceiptOperator.toProto();
      long totalAmountCents = receiptItems.stream()
          .mapToLong(receiptItem -> receiptItem.getItemPriceCents() * receiptItem.getItemQuantity())
          .sum();
      return GetReceiptResponse.newBuilder()
          .setReceipt(Receipt.newBuilder()
              .addAllItems(receiptItems)
              .setTotalAmountCents(totalAmountCents)
              .build())
          .build();
    });
  }
}
