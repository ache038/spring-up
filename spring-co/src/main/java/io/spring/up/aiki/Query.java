package io.spring.up.aiki;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.spring.up.query.Inquiry;
import io.vertx.core.json.JsonObject;

class Query<T> {

    private final transient Inquiry inquiry;
    private transient EntityPathBase<T> entity;
    private transient JPAQueryFactory factory;

    public static <I> Query<I> create(final JsonObject input) {
        return new Query<>(input);
    }

    private Query(final JsonObject input) {
        this.inquiry = Inquiry.create(input);
    }

    public Query<T> on(final EntityPathBase<T> entity) {
        this.entity = entity;
        return this;
    }

    public Query<T> on(final JPAQueryFactory factory) {
        this.factory = factory;
        return this;
    }

    public Query<T> debug() {
        System.out.println(this.inquiry.toJson());
        return this;
    }
}
