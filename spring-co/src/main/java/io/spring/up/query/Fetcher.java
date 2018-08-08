package io.spring.up.query;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import io.vertx.core.json.JsonObject;
import io.vertx.up.atom.query.Inquiry;

public interface Fetcher<T> {

    JsonObject search(final Predicate predicate);

    Fetcher bind(EntityPathBase<T> entityCls);

    Fetcher bind(Inquiry inquiry);
}
