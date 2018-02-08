package com.washup.app.api.v1.orders;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.washup.app.AbstractTest;
import com.washup.app.AppTester;
import com.washup.app.TestUsers;
import com.washup.app.orders.OrderTester;
import com.washup.app.spring.DateUtils;
import com.washup.app.tokens.Token;
import com.washup.protos.App.GetOrdersRequest;
import com.washup.protos.App.GetOrdersResponse;
import com.washup.protos.App.Order;
import com.washup.protos.App.PlaceOrderRequest;
import com.washup.protos.App.PlaceOrderResponse;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import java.time.Clock;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderTest extends AbstractTest {
  @Autowired
  AppTester.Factory factory;

  @Autowired
  Clock clock;

  @Autowired
  OrderTester.Factory orderTesterFactory;

  @Test public void placeRushOrder() throws Exception {
    AppTester markApp = factory.signup(TestUsers.MARK);
    long deliveryMillis = new DateTime(clock.millis()).plus(Duration.standardHours(24)).getMillis();
    PlaceOrderResponse placeOrderResponse = markApp.placeOrder(PlaceOrderRequest.newBuilder()
        .setOrderType(OrderType.WASH_FOLD)
        .setPickupDate(clock.millis())
        .setDeliveryDate(deliveryMillis)
        .build());
    // Proto converter is converting instances with no attributes to null
    assertThat(placeOrderResponse).isNull();
    OrderTester orderTester = orderTesterFactory.last();
    orderTester.assertOrderType().isEqualTo(OrderType.WASH_FOLD);
    orderTester.assertStatus().isEqualTo(OrderStatus.PENDING);
    orderTester.assertPickupDate().isEqualToIgnoringHours(new Date(clock.millis()));
    orderTester.assertDeliveryDate().isEqualToIgnoringHours(new Date(deliveryMillis));
    orderTester.assertRushService().isEqualTo(true);
    orderTester.assertTotalCostCents().isEqualTo(0L);
  }

  @Test public void placeNonRushOrder() throws Exception {
    AppTester markApp = factory.signup(TestUsers.MARK);
    long deliveryMillis = new DateTime(clock.millis()).plus(Duration.standardHours(48)).getMillis();
    PlaceOrderResponse placeOrderResponse = markApp.placeOrder(PlaceOrderRequest.newBuilder()
        .setOrderType(OrderType.WASH_FOLD)
        .setPickupDate(clock.millis())
        .setDeliveryDate(deliveryMillis)
        .build());
    // Proto converter is converting instances with no attributes to null
    assertThat(placeOrderResponse).isNull();
    OrderTester orderTester = orderTesterFactory.last();
    orderTester.assertOrderType().isEqualTo(OrderType.WASH_FOLD);
    orderTester.assertStatus().isEqualTo(OrderStatus.PENDING);
    orderTester.assertPickupDate().isEqualToIgnoringHours(new Date(clock.millis()));
    orderTester.assertDeliveryDate().isEqualToIgnoringHours(new Date(deliveryMillis));
    orderTester.assertRushService().isEqualTo(false);
    orderTester.assertTotalCostCents().isEqualTo(0L);
  }

  @Test public void getOrder() throws Exception {
    AppTester markApp = factory.signup(TestUsers.MARK);
    DateTime currentDateTime = new DateTime(clock.millis());
    DateTime deliveryDate = new DateTime(clock.millis())
        .plus(Duration.standardHours(24));
    markApp.placeOrder(PlaceOrderRequest.newBuilder()
        .setOrderType(OrderType.WASH_FOLD)
        .setPickupDate(currentDateTime.getMillis())
        .setIdempotenceToken(Token.generateToken())
        .setDeliveryDate(deliveryDate.getMillis())
        .build());

    DateTime dryCleanPickUpDate = new DateTime(clock.millis()).plus(Duration.standardHours(48));
    DateTime dryCleanDeliveryDate = new DateTime(clock.millis()).plus(Duration.standardHours(96));
    markApp.placeOrder(PlaceOrderRequest.newBuilder()
        .setOrderType(OrderType.DRY_CLEAN)
        .setPickupDate(dryCleanPickUpDate.getMillis())
        .setIdempotenceToken(Token.generateToken())
        .setDeliveryDate(dryCleanDeliveryDate.getMillis())
        .build());

    GetOrdersResponse order = markApp.getOrder(GetOrdersRequest.newBuilder().build());
    List<OrderTester> orderTester = orderTesterFactory.all();
    List<Order> ordersList = order.getOrdersList();
    assertThat(ordersList).isEqualTo(ImmutableList.of(
        Order.newBuilder()
            .setPickupDate(DateUtils.roundedMillis(dryCleanPickUpDate))
            .setDeliveryDate(DateUtils.roundedMillis(dryCleanDeliveryDate))
            .setStatus(OrderStatus.PENDING)
            .setOrderType(OrderType.DRY_CLEAN)
            .setToken(orderTester.get(0).getOrderToken().getId())
            .build(),
        Order.newBuilder()
            .setRushService(true)
            .setPickupDate(DateUtils.roundedMillis(currentDateTime))
            .setDeliveryDate(DateUtils.roundedMillis(deliveryDate))
            .setStatus(OrderStatus.PENDING)
            .setOrderType(OrderType.WASH_FOLD)
            .setToken(orderTester.get(1).getOrderToken().getId())
            .build()));
  }
}
