package com.washup.app;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.washup.app.users.UserTester;
import com.washup.protos.App;
import com.washup.protos.App.GetOrdersRequest;
import com.washup.protos.App.GetOrdersResponse;
import com.washup.protos.App.LoginRequest;
import com.washup.protos.App.LoginResponse;
import com.washup.protos.App.PlaceOrderRequest;
import com.washup.protos.App.PlaceOrderResponse;
import com.washup.protos.App.SignUpRequest;
import com.washup.protos.App.SignUpResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

public class AppTester {
  private final TestUser testUser;
  private final UserTester.Factory userTesterFactory;
  private TestRestTemplate testRestTemplate;

  private String authToken;
  private static final String PASSWORD = "PASSWORD123";

  private AppTester(Factory factory, TestUser testUser) {
    this.testUser = testUser;
    this.testRestTemplate = factory.testRestTemplate;
    this.userTesterFactory = factory.userTesterFactory;
    this.authToken = null;
  }

  public void resetAuthToken() {
    this.authToken = null;
  }

  public UserTester userTester() {
    return userTesterFactory.get(testUser.getEmail());
  }

  public void signup() {
    SignUpRequest request = SignUpRequest.newBuilder()
        .setFirstName(testUser.getFirstName())
        .setLastName(testUser.getLastName())
        .setEmail(testUser.getEmail())
        .setPassword(PASSWORD)
        .setPhoneNumber(testUser.getPhoneNumber())
        .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpEntity<App.SignUpRequest> httpRequest = new HttpEntity<>(request, httpHeaders);
    ResponseEntity<SignUpResponse> httpResponse = testRestTemplate
        .postForEntity("/api/v1/users/sign-up", httpRequest, SignUpResponse.class);
    List<String> authorization = httpResponse.getHeaders().get("Authorization");
    checkState(authorization != null && !authorization.isEmpty(), "Unable to authenticate");
    // Save the auth token
    this.authToken = authorization.get(0);
  }

  public void login() {
    LoginRequest request = LoginRequest.newBuilder()
        .setEmail(testUser.getEmail())
        .setPassword(PASSWORD)
        .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    HttpEntity<App.LoginRequest> httpRequest = new HttpEntity<>(request, httpHeaders);
    ResponseEntity<LoginResponse> httpResponse = testRestTemplate
        .postForEntity("/api/v1/users/login", httpRequest, LoginResponse.class);
    List<String> authorization = httpResponse.getHeaders().get("Authorization");
    checkState(authorization != null && !authorization.isEmpty(), "Unable to login");
    // Save the auth token
    this.authToken = authorization.get(0);
  }

  public PlaceOrderResponse placeOrder(PlaceOrderRequest request) {
    return post("/api/v1/orders/place-order", request, PlaceOrderResponse.class);
  }

  public GetOrdersResponse getOrder(GetOrdersRequest request) {
    return get("/api/v1/orders/get-orders", request, GetOrdersResponse.class);
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
    UserTester.Factory userTesterFactory;

    public AppTester get(TestUser testUser) {
      return new AppTester(this, testUser);
    }

    public AppTester signup(TestUser testUser) {
      AppTester appTester = get(testUser);
      appTester.signup();
      return appTester;
    }
  }
}
