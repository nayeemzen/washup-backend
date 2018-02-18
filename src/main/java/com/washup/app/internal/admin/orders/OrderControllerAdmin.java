package com.washup.app.internal.admin.orders;

import static com.washup.app.internal.admin.AdminConstants.ADMIN_URL;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.orders.OrderOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.app.orders.OrderToken;
import com.washup.protos.Admin.GetOrderDataAdmin;
import com.washup.protos.Admin.GetOrderRequestAdmin;
import com.washup.protos.Admin.GetOrderResponseAdmin;
import com.washup.protos.Admin.GetOrdersRequestAdmin;
import com.washup.protos.Admin.GetOrdersResponseAdmin;
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
    List<GetOrderDataAdmin> orders = transacter.call(session -> {
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
          .setOrderData(GetOrderDataAdmin.newBuilder()
              .setOrder(orderOperator.toInternal())
              .setUser(orderOperator.getUser().toInternal(session))
              .build())
          .build();
    });
  }

  private GetOrderDataAdmin toData(Session session, OrderOperator order) {
    return GetOrderDataAdmin.newBuilder()
        .setOrder(order.toInternal())
        .setUser(order.getUser().toInternal(session))
        .build();
  }
}
