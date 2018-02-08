package com.washup.app.database.hibernate;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

public abstract class AbstractQuery<T, R extends AbstractQuery, S extends AbstractOperator<T>> {

  protected final Session session;
  protected final Criteria criteria;
  protected final Constructor<S> constructor;

  public AbstractQuery(Session session, Class<T> classType, Class<S> operatorType) {
    this.session = session;
    this.criteria = session.createCriteria(classType);
    try {
      this.constructor = operatorType.getConstructor(Session.class, classType);
    } catch (NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public R orderAsc(String propertyName) {
    criteria.addOrder(Order.asc(propertyName));
    return (R) this;
  }

  public R orderDesc(String propertyName) {
    criteria.addOrder(Order.desc(propertyName));
    return (R) this;
  }

  public List<S> list() {
    @SuppressWarnings("unchecked")
    List<T> list = criteria.list();
    return list.stream()
        .map(t -> construct(t))
        .collect(Collectors.toList());
  }

  public @Nullable S uniqueResult() {
    T result = (T) criteria.uniqueResult();
    return result != null ? construct(result) : null;
  }

  private S construct(T result) {
    try {
      return constructor.newInstance(session, result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
