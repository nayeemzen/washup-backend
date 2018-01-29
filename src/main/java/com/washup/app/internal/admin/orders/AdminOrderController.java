package com.washup.app.internal.admin.orders;

import static com.washup.app.internal.admin.AdminConstants.ADMIN_URL;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.protos.Internal;
import com.washup.protos.Shared.Order;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AdminOrderController.URL)
public class AdminOrderController {

  static final String URL = ADMIN_URL + "/orders";

  @Autowired
  Transacter transacter;

  @Autowired
  OrderQuery.Factory orderQueryFactory;

  @Autowired
  WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;

  @GetMapping("/get-orders")
  public Internal.GetOrderResponse getOrders(@RequestBody Internal.GetOrdersRequest request,
      Authentication authentication) {
    List<Order> orders = transacter.call(session -> {
      WashUpEmployeeOperator authenticatedEmployee = washUpEmployeeOperatorFactory
          .getAuthenticatedEmployee(session, authentication);
      ParametersChecker.check(
          request.getStartDate() > 0 && request.getEndDate() > request.getStartDate(),
          "invalid start and end dates");
      DateTime startDate = new DateTime(request.getStartDate());
      DateTime endDate = new DateTime(request.getEndDate());
      OrderQuery orderQuery = orderQueryFactory.get(session);
      orderQuery.isBilled(request.getBilled());
      orderQuery.ordersBetween(startDate, endDate);
      if (request.getOrderStatus() != null) {
        orderQuery.orderStatus(request.getOrderStatus());
      }
      if (request.getOrderType() != null) {
        orderQuery.orderType(request.getOrderType());
      }
      return orderQuery.list().stream()
          .map(o -> o.toWire())
          .collect(Collectors.toList());
    });

    return Internal.GetOrderResponse.newBuilder()
        .addAllOrders(orders)
        .build();
  }
}
