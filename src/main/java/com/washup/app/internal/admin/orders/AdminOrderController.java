package com.washup.app.internal.admin.orders;

import static com.washup.app.internal.admin.AdminConstants.ADMIN_URL;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.protos.Admin.GetOrderResponseInternal;
import com.washup.protos.Admin.GetOrdersRequestInternal;
import com.washup.protos.Admin.OrderInternal;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
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

  @PostMapping("/get-orders-internal")
  public GetOrderResponseInternal getOrdersInternal(@RequestBody GetOrdersRequestInternal request,
      Authentication authentication) {
    List<OrderInternal> orders = transacter.call(session -> {
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
      if (request.getOrderStatus() != OrderStatus.STATUS_UNKNOWN) {
        orderQuery.orderStatus(request.getOrderStatus());
      }
      if (request.getOrderType() != OrderType.TYPE_UNKNOWN) {
        orderQuery.orderType(request.getOrderType());
      }
      return orderQuery.orderAsc("id")
          .list()
          .stream()
          .map(o -> o.toInternal())
          .collect(Collectors.toList());
    });

    return GetOrderResponseInternal.newBuilder()
        .addAllOrders(orders)
        .build();
  }
}
