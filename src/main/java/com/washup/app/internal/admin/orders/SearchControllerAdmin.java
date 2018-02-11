package com.washup.app.internal.admin.orders;

import static com.washup.app.internal.admin.AdminConstants.ADMIN_URL;

import com.google.common.base.Strings;
import com.washup.app.database.hibernate.Transacter;
import com.washup.app.exception.ParametersChecker;
import com.washup.app.internal.admin.washup_employees.WashUpEmployeeOperator;
import com.washup.app.orders.OrderQuery;
import com.washup.protos.Admin.SearchRequestAdmin;
import com.washup.protos.Admin.SearchResponseAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SearchControllerAdmin.URL)
public class SearchControllerAdmin {

  static final String URL = ADMIN_URL + "/search";

  @Autowired
  Transacter transacter;

  @Autowired
  OrderQuery.Factory orderQueryFactory;

  @Autowired
  WashUpEmployeeOperator.Factory washUpEmployeeOperatorFactory;

  @PostMapping("/search")
  public SearchResponseAdmin search(@RequestBody SearchRequestAdmin request,
      Authentication authentication) {
    String keyword = request.getKeyword();
    ParametersChecker.check(!Strings.isNullOrEmpty(keyword), "keyword must not be null");
    if (keyword.startsWith("#")) {
      return SearchResponseAdmin.newBuilder()
          .setRedirectPath("/order/" + keyword.substring(1))
          .build();
    }
    if (keyword.startsWith("U_")) {
      return SearchResponseAdmin.newBuilder()
          .setRedirectPath("/user/" + keyword)
          .build();
    }

    return SearchResponseAdmin.newBuilder()
        .build();
  }
}
