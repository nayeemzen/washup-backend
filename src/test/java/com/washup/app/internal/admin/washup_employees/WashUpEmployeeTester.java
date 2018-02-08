package com.washup.app.internal.admin.washup_employees;

import static com.google.common.base.Preconditions.checkNotNull;

import com.washup.app.database.hibernate.Id;
import com.washup.app.database.hibernate.Transacter;
import javax.annotation.Nullable;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class WashUpEmployeeTester {

  private final Transacter transacter;
  private final Id<DbWashUpEmployee> washUpEmployeeId;

  public WashUpEmployeeTester(Factory factory, Id<DbWashUpEmployee> washUpEmployeeId) {
    this.transacter = factory.transacter;
    this.washUpEmployeeId = checkNotNull(washUpEmployeeId);
  }

  public Id<DbWashUpEmployee> getId() {
    return washUpEmployeeId;
  }

  public WashUpEmployeeToken getToken() {
    return transacter.call(session -> load(session).getToken());
  }

  private DbWashUpEmployee load(Session session) {
    return session.load(DbWashUpEmployee.class, washUpEmployeeId.getId());
  }

  @Component
  public static class Factory {
    @Autowired
    Transacter transacter;

    @Autowired
    WashUpEmployeeOperator.Factory washUpEmployeeOperator;

    public @Nullable
    WashUpEmployeeTester get(WashUpEmployeeToken washUpEmployeeToken) {
      Id<DbWashUpEmployee> washUpEmployeeId = transacter.call(session -> {
        WashUpEmployeeOperator employeeOperator = washUpEmployeeOperator
            .get(session, washUpEmployeeToken);
        return employeeOperator != null ? employeeOperator.getId() : null;
      });
      return washUpEmployeeId != null ? new WashUpEmployeeTester(this, washUpEmployeeId) : null;
    }
  }
}
