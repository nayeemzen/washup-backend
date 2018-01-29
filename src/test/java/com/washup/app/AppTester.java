package com.washup.app;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.washup.protos.App;
import com.washup.protos.App.SignUpRequest;
import com.washup.protos.App.SignUpResponse;
import java.util.List;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class AppTester {
  private TestUser testUser;
  private TestRestTemplate testRestTemplate;

  private String authToken;

  public AppTester(TestUser testUser, TestRestTemplate testRestTemplate) {
    this.testUser = testUser;
    this.testRestTemplate = testRestTemplate;
    this.authToken = null;
  }

  public void signup() {
    SignUpRequest request = SignUpRequest.newBuilder()
        .setFirstName(testUser.getFirstName())
        .setLastName(testUser.getLastName())
        .setEmail(testUser.getEmail())
        .setPassword("123")
        .setPhoneNumber(testUser.getPhoneNumber())
        .build();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Accepts", "application/x-protobuf");
    HttpEntity<App.SignUpRequest> httpRequest = new HttpEntity<>(request, httpHeaders);
    ResponseEntity<SignUpResponse> httpResponse = testRestTemplate
        .postForEntity("/api/v1/users/sign-up", httpRequest, SignUpResponse.class);
    List<String> authorization = httpResponse.getHeaders().get("Authorization");
    checkState(authorization != null, "Unable to authenticate");
  }

  private <T, R> T post(String uri, R request, Class<T> responseType) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Accepts", "application/x-protobuf");
    HttpEntity<R> httpRequest = new HttpEntity<R>(request, httpHeaders);
    ResponseEntity<T> httpResponse = testRestTemplate.postForEntity(uri, httpRequest, responseType);
    return httpResponse.getBody();
  }

  public static class Factory {
    private final TestRestTemplate testRestTemplate;

    public Factory(TestRestTemplate testRestTemplate) {
      this.testRestTemplate = testRestTemplate;
    }

    public AppTester get(TestUser testUser) {
      return new AppTester(testUser, testRestTemplate);
    }
  }
}
