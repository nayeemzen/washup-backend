package com.washup.app.internal.admin.orders;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.washup.app.AbstractTest;
import com.washup.app.AppTester;
import com.washup.app.TestUsers;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeAppTester;
import com.washup.app.orders.OrderTester;
import com.washup.app.orders.OrderToken;
import com.washup.app.tokens.Token;
import com.washup.protos.Admin.GetOrderResponseInternal;
import com.washup.protos.Admin.GetOrdersRequestInternal;
import com.washup.protos.Admin.OrderInternal;
import com.washup.protos.App.PlaceOrderRequest;
import com.washup.protos.App.PlaceOrderResponse;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminOrdersTest extends AbstractTest {
  @Autowired
  WashUpEmployeeAppTester.Factory washUpEmployeeAppTesterFactory;

  @Autowired
  AppTester.Factory appTesterFactory;

  @Autowired
  OrderTester.Factory orderTesterFactory;

  @Test
  public void getOrdersTest() throws Exception {
    AppTester markApp = appTesterFactory.signup(TestUsers.MARK);
    AppTester thomasApp = appTesterFactory.signup(TestUsers.THOMAS);

    DateTime currentDateTime = DateTime.parse("2018-02-07T03:00:00");
    DateTime deliveryDate = currentDateTime.plus(Duration.standardHours(24));

    // Mark place's his first order today
    PlaceOrderResponse marksOrder = markApp.placeOrder(PlaceOrderRequest.newBuilder()
        .setOrderType(OrderType.WASH_FOLD)
        .setPickupDate(currentDateTime.getMillis())
        .setIdempotenceToken(Token.generateToken())
        .setDeliveryDate(deliveryDate.getMillis())
        .build());

    DateTime currentDateTime1 = currentDateTime.plus(Duration.standardHours(42));
    DateTime deliveryDate1 = new DateTime(currentDateTime).plus(Duration.standardHours(92));
    // Thomas place's his dry cleaning order for tomorrow.
    PlaceOrderResponse thomasOrder = thomasApp.placeOrder(PlaceOrderRequest.newBuilder()
        .setOrderType(OrderType.DRY_CLEAN)
        .setPickupDate(currentDateTime1.getMillis())
        .setIdempotenceToken(Token.generateToken())
        .setDeliveryDate(deliveryDate1.getMillis())
        .build());

    WashUpEmployeeAppTester washUpEmployeeAppTester = washUpEmployeeAppTesterFactory.create();
    OrderTester markOrderTester = orderTesterFactory.get(
        new OrderToken(marksOrder.getOrder().getToken()));
    OrderTester thomasOrderTester = orderTesterFactory.get(
        new OrderToken(thomasOrder.getOrder().getToken()));

    // Only marks order is visible during 02-07
    GetOrderResponseInternal order = washUpEmployeeAppTester.getOrders(
        GetOrdersRequestInternal.newBuilder()
            .setStartDate(DateTime.parse("2018-02-07T00:00:00").getMillis())
            .setEndDate(DateTime.parse("2018-02-07T23:59:59").getMillis())
            .build());
    assertThat(order.getOrdersList()).isEqualTo(ImmutableList.of(OrderInternal.newBuilder()
        .setUserToken(markApp.userTester().getToken().getId())
        .setBilledAt(0)
        .setStatus(OrderStatus.PENDING)
        .setToken(marksOrder.getOrder().getToken())
        .setOrderType(OrderType.WASH_FOLD)
        .setPickupDate(currentDateTime.getMillis())
        .setDeliveryDate(deliveryDate.getMillis())
        .setRushService(true)
        .setTotalCostCents(0)
        .setCreatedAt(markOrderTester.getCreatedAt().toInstant().toEpochMilli())
        .setUpdatedAt(markOrderTester.getUpdateAt().toInstant().toEpochMilli())
        .build()));


    // No dry cleaning orders are visible during 02-07
    GetOrderResponseInternal noDryCleaningOrders = washUpEmployeeAppTester.getOrders(
        GetOrdersRequestInternal.newBuilder()
            .setStartDate(DateTime.parse("2018-02-07T00:00:00").getMillis())
            .setEndDate(DateTime.parse("2018-02-07T23:59:59").getMillis())
            .setOrderType(OrderType.DRY_CLEAN)
            .build());
    assertThat(noDryCleaningOrders).isNull();

    // marks & thomas orders are visible during 02-07 and 02-08
    GetOrderResponseInternal allOrders = washUpEmployeeAppTester.getOrders(
        GetOrdersRequestInternal.newBuilder()
            .setStartDate(DateTime.parse("2018-02-07T00:00:00").getMillis())
            .setEndDate(DateTime.parse("2018-02-08T23:59:59").getMillis())
            .build());
    assertThat(allOrders.getOrdersList()).isEqualTo(ImmutableList.of(
        OrderInternal.newBuilder()
            .setUserToken(markApp.userTester().getToken().getId())
            .setBilledAt(0)
            .setStatus(OrderStatus.PENDING)
            .setToken(marksOrder.getOrder().getToken())
            .setOrderType(OrderType.WASH_FOLD)
            .setPickupDate(currentDateTime.getMillis())
            .setDeliveryDate(deliveryDate.getMillis())
            .setRushService(true)
            .setTotalCostCents(0)
            .setCreatedAt(markOrderTester.getCreatedAt().toInstant().toEpochMilli())
            .setUpdatedAt(markOrderTester.getUpdateAt().toInstant().toEpochMilli())
            .build(),
        OrderInternal.newBuilder()
            .setUserToken(thomasApp.userTester().getToken().getId())
            .setBilledAt(0)
            .setStatus(OrderStatus.PENDING)
            .setToken(thomasOrder.getOrder().getToken())
            .setOrderType(OrderType.DRY_CLEAN)
            .setPickupDate(currentDateTime1.getMillis())
            .setDeliveryDate(deliveryDate1.getMillis())
            .setRushService(false)
            .setTotalCostCents(0)
            .setCreatedAt(thomasOrderTester.getCreatedAt().toInstant().toEpochMilli())
            .setUpdatedAt(thomasOrderTester.getUpdateAt().toInstant().toEpochMilli())
            .build()));
  }
}
