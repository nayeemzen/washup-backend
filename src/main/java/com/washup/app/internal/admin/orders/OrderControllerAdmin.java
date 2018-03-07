package com.washup.app.internal.admin.orders;

import static com.washup.app.internal.admin.AdminConstants.ADMIN_URL;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.orders.OrderOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.app.orders.OrderToken;
import com.washup.protos.Admin.GetOrderRequestAdmin;
import com.washup.protos.Admin.GetOrderResponseAdmin;
import com.washup.protos.Admin.GetOrdersFeedRequest;
import com.washup.protos.Admin.GetOrdersFeedResponse;
import com.washup.protos.Admin.GetOrdersRequestAdmin;
import com.washup.protos.Admin.GetOrdersResponseAdmin;
import com.washup.protos.Admin.OrderDataAdmin;
import com.washup.protos.Admin.UpdateOrderStatusRequest;
import com.washup.protos.Admin.UpdateOrderStatusResponse;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(OrderControllerAdmin.URL)
public class OrderControllerAdmin {

  static final String URL = ADMIN_URL + "/orders";

  @Autowired
  Transacter transacter;

  @Autowired
  OrderQuery.Factory orderQueryFactory;

  @Autowired
  OrderOperator.Factory orderOperatorFactory;

  @Autowired
  WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;

  @PostMapping("/get-orders-admin")
  public GetOrdersResponseAdmin getOrdersInternal(@RequestBody GetOrdersRequestAdmin request,
      Authentication authentication) {
    List<OrderDataAdmin> orders = transacter.call(session -> {
      WashUpEmployeeOperator authenticatedEmployee = washUpEmployeeOperatorFactory
          .getAuthenticatedEmployee(session, authentication);
      ParametersChecker.check(
          request.getStartDate() > 0 && request.getEndDate() >= request.getStartDate(),
          "invalid start and end dates");
      DateTime startDate = new DateTime(request.getStartDate());
      DateTime endDate = new DateTime(request.getEndDate());
      OrderQuery orderQuery = orderQueryFactory.get(session);
      orderQuery.isBilled(request.getBilled());
      orderQuery.ordersBetween(startDate, endDate);
      if (request.getOrderStatus() != OrderStatus.STATUS_UNKNOWN) {
        orderQuery.orderStatus(request.getOrderStatus());
      }
      if (request.getOrderType() != OrderType.TYPE_UNKNOWN) {
        orderQuery.orderType(request.getOrderType());
      }
      return orderQuery.orderAsc("id")
          .list()
          .stream()
          .map(o -> toData(session, o))
          .collect(Collectors.toList());
    });

    return GetOrdersResponseAdmin.newBuilder()
        .addAllOrders(orders)
        .build();
  }

  @PostMapping("/get-order-admin")
  public GetOrderResponseAdmin getOrderAdmin(@RequestBody GetOrderRequestAdmin request,
      Authentication authentication) {
    ParametersChecker.check(!Strings.isNullOrEmpty(request.getOrderToken()), "order_token missing");
    return transacter.call(session -> {
      WashUpEmployeeOperator authenticatedEmployee = washUpEmployeeOperatorFactory
          .getAuthenticatedEmployee(session, authentication);
      OrderOperator orderOperator = orderOperatorFactory
          .get(session, new OrderToken("#" + request.getOrderToken()));
      ParametersChecker.check(orderOperator != null, "No order found");
      return GetOrderResponseAdmin.newBuilder()
          .setOrderData(OrderDataAdmin.newBuilder()
              .setOrder(orderOperator.toInternal())
              .setUser(orderOperator.getUser().toInternal(session))
              .build())
          .build();
    });
  }

  @PostMapping("get-orders-feed")
  public GetOrdersFeedResponse getOrdersFeed(@RequestBody GetOrdersFeedRequest request,
      Authentication authentication) {
    return transacter.call(session -> {
      List<OrderOperator> orderOperator = orderQueryFactory.get(session)
          .orderByDesc("createdAt")
          .limit(Math.min(request.getMaxOrders(), 100))
          .list();
      List<OrderDataAdmin> orders = orderOperator.stream()
          .map(m -> OrderDataAdmin.newBuilder()
              .setUser(m.getUser().toInternal(session))
              .setOrder(m.toInternal())
              .build())
          .collect(Collectors.toList());
      return GetOrdersFeedResponse.newBuilder()
          .addAllOrders(orders)
          .build();
    });
  }

  @PostMapping("update-status")
  public UpdateOrderStatusResponse getOrdersFeed(@RequestBody UpdateOrderStatusRequest request,
      Authentication authentication) {
    return transacter.call(session -> {
      OrderOperator orderOperator = orderOperatorFactory
          .get(session, new OrderToken(request.getOrderToken()));
      orderOperator.setStatus(request.getOrderStatus())
          .update();
      return UpdateOrderStatusResponse.newBuilder()
          .setOrderStatus(orderOperator.getStatus())
          .build();
    });
  }

  private OrderDataAdmin toData(Session session, OrderOperator order) {
    return OrderDataAdmin.newBuilder()
        .setOrder(order.toInternal())
        .setUser(order.getUser().toInternal(session))
        .build();
  }
}
