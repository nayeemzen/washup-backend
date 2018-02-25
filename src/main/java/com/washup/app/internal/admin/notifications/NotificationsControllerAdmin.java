package com.washup.app.internal.admin.notifications;

import static com.google.common.base.Preconditions.checkState;
import static com.washup.app.internal.admin.AdminConstants.ADMIN_URL;

import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.notifications.sms.SmsNotificationService;
import com.washup.app.orders.OrderOperator;
import com.washup.app.orders.OrderToken;
import com.washup.app.users.UserOperator;
import com.washup.protos.Admin.SendValetArrivingNotificationRequestAdmin;
import com.washup.protos.Admin.SendValetArrivingNotificationResponseAdmin;
import com.washup.protos.Shared.OrderStatus;
import com.washup.protos.Shared.OrderType;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(NotificationsControllerAdmin.URL)
public class NotificationsControllerAdmin {
  static final String URL = ADMIN_URL + "/notifications";

  private static final String PICKUP_MESSAGE =
      "Your WashUp valet %s will be there shortly to pickup your cloths for %s.";
  private static final String DROPOFF_MESSAGE =
      "Your WashUp valet %s will be there shortly to drop off your freshly cleaned cloths!";

  @Autowired
  Transacter transacter;

  @Autowired
  OrderOperator.Factory orderOperatorFactory;

  @Autowired
  WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;

  @Autowired
  SmsNotificationService smsNotificationService;

  @PostMapping("/notify-valet-arriving")
  public SendValetArrivingNotificationResponseAdmin sendValetArrivingNotification(
      @RequestBody SendValetArrivingNotificationRequestAdmin request,
      Authentication authentication) {
    ArrivingMessage message = transacter.call(session -> {
      WashUpEmployeeOperator authenticatedEmployee = washUpEmployeeOperatorFactory
          .getAuthenticatedEmployee(session, authentication);
      ParametersChecker.check(request.getOrderToken() != null, "order token cannot be null");
      OrderOperator orderOperator = orderOperatorFactory
          .get(session, new OrderToken(request.getOrderToken()));
      checkState(orderOperator != null);
      UserOperator userOperator = new UserOperator(session, orderOperator.getUser());
      String orderType = orderOperator.getOrderType() == OrderType.DRY_CLEAN
          ? "dry cleaning"
          : "wash & fold";
      String employeeFirstName = Strings.capitalize(authenticatedEmployee.getFirstName());
      if (orderOperator.getStatus() == OrderStatus.PENDING) {
        return new ArrivingMessage(userOperator.getPhoneNumber(),
            String.format(PICKUP_MESSAGE, employeeFirstName, orderType));
      } else if (orderOperator.getStatus() == OrderStatus.PICKED_UP) {
        return new ArrivingMessage(userOperator.getPhoneNumber(),
            String.format(DROPOFF_MESSAGE, employeeFirstName));
      } else {
        throw new IllegalStateException(
            String.format("illegal state %s", orderOperator.getStatus()));
      }
    });

    smsNotificationService.send(message.phoneNumber, message.message);

    return SendValetArrivingNotificationResponseAdmin.newBuilder()
        .build();
  }

  private final class ArrivingMessage {
    public final String phoneNumber;
    public final String message;

    public ArrivingMessage(String phoneNumber, String message) {
      this.phoneNumber = phoneNumber;
      this.message = message;
    }
  }
}
