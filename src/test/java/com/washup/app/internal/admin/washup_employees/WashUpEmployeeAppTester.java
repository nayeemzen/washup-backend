package com.washup.app.internal.admin.washup_employees;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.washup.app.database.hibernate.Transacter;
import com.washup.protos.Admin.GetOrderResponseInternal;
import com.washup.protos.Admin.GetOrdersRequestInternal;
import com.washup.protos.App.LoginRequest;
import com.washup.protos.App.LoginResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

public class WashUpEmployeeAppTester {
  private final WashUpEmployeeTester.Factory washUpEmployeeTesterFactory;
  private TestRestTemplate testRestTemplate;

  private final WashUpEmployeeToken washUpEmployeeToken;
  private String authToken;
  private static final String PASSWORD = "PASSWORD123";
  private static final String EMAIL = "david@washup.io";

  private WashUpEmployeeAppTester(Factory factory, WashUpEmployeeToken washUpEmployeeToken) {
    this.testRestTemplate = factory.testRestTemplate;
    this.washUpEmployeeTesterFactory = factory.washUpEmployeeTesterFactory;
    this.washUpEmployeeToken = washUpEmployeeToken;
    this.authToken = null;
  }

  public WashUpEmployeeTester washUpEmployeeTester() {
    return washUpEmployeeTesterFactory.get(washUpEmployeeToken);
  }

  public void login() {
    LoginRequest request = LoginRequest.newBuilder()
        .setEmail(EMAIL)
        .setPassword(PASSWORD)
        .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpEntity<LoginRequest> httpRequest = new HttpEntity<>(request, httpHeaders);
    ResponseEntity<LoginResponse> httpResponse = testRestTemplate
        .postForEntity("/_admin/login", httpRequest, LoginResponse.class);
    List<String> authorization = httpResponse.getHeaders().get("Authorization");
    checkState(authorization != null && !authorization.isEmpty(), "Unable to login");
    // Save the auth token
    this.authToken = authorization.get(0);
  }

  public GetOrderResponseInternal getOrders(GetOrdersRequestInternal request) {
    return post("/_admin/orders/get-orders-internal", request, GetOrderResponseInternal.class);
  }

  private <T, R> T post(String uri, R request, Class<T> responseType) {
    return call(uri, request, responseType, false);
  }

  private <T, R> T get(String uri, R request, Class<T> responseType) {
    return call(uri, request, responseType, true);
  }

  private <T, R> T call(String uri, R request, Class<T> responseType, boolean isGet) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.put("Authorization", ImmutableList.of(authToken));
    HttpEntity<R> httpRequest = new HttpEntity<>(request, httpHeaders);
    ResponseEntity<T> httpResponse = isGet
        ? testRestTemplate.exchange(uri, HttpMethod.GET, httpRequest, responseType)
        : testRestTemplate.postForEntity(uri, httpRequest, responseType);
    checkState(httpResponse.getStatusCode() == HttpStatus.OK);
    return httpResponse.getBody();
  }

  @Component
  public static class Factory {
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;

    @Autowired
    Transacter transacter;

    @Autowired
    WashUpEmployeeTester.Factory washUpEmployeeTesterFactory;

    public WashUpEmployeeAppTester onlyCreate() {
      WashUpEmployeeToken token = transacter.call(session -> {
        WashUpEmployeeOperator employeeOperator = washUpEmployeeOperatorFactory
            .create(session, "David", "Austin", EMAIL, PASSWORD);
        return employeeOperator.getToken();
      });
      return new WashUpEmployeeAppTester(this, token);
    }

    public WashUpEmployeeAppTester create() {
      WashUpEmployeeAppTester washUpEmployeeAppTester = onlyCreate();
      washUpEmployeeAppTester.login();
      return washUpEmployeeAppTester;
    }
  }
}
