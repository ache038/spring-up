package io.spring.up.query.fetcher;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import io.spring.up.query.Fetcher;
import io.vertx.core.json.JsonObject;
import io.vertx.up.atom.query.Inquiry;

public class MongoFetcher<T> implements Fetcher<T> {
    private transient Inquiry inquiry;
    private transient EntityPathBase<T> entity;

    @Override
    public JsonObject search(final Predicate predicate) {
        return null;
    }

    @Override
    public Fetcher bind(final EntityPathBase<T> entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public Fetcher bind(final Inquiry inquiry) {
        this.inquiry = inquiry;
        return this;
    }
}
