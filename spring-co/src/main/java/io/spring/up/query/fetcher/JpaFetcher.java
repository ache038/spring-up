package io.spring.up.query.fetcher;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.spring.up.log.Log;
import io.spring.up.query.Fetcher;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.atom.query.Inquiry;
import io.vertx.up.atom.query.Pager;
import io.zero.epic.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JpaFetcher<T> implements Fetcher<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaFetcher.class);
    private final transient JPAQueryFactory factory;
    private transient Inquiry inquiry;
    private transient EntityPathBase<T> entity;

    public JpaFetcher(final JPAQueryFactory factory) {
        this.factory = factory;
    }

    @Override
    public JsonObject search(final Predicate predicate) {
        final List<T> entities = this.searchAdvanced(predicate);
        final Long count = this.countAvanced(predicate);
        final JsonObject result = new JsonObject();
        final JsonArray listData = Ut.serializeJson(entities);
        result.put("list", listData);
        result.put("count", count);
        return result;
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


    private List<T> searchAdvanced(final Predicate predicate) {
        JPAQuery<T> query = this.factory.selectFrom(this.entity);
        Log.info(LOGGER, "[ UP ] [QE] Criteria = {0}", null == predicate ? null : predicate.toString());
        // 条件处理
        if (null != predicate) {
            query = query.where(predicate);
        }
        // 排序处理
        query = this.getOrderBy(query);
        // 分页处理
        query = this.getPager(query);
        return query.fetch();
    }

    private Long countAvanced(final Predicate predicate) {
        JPAQuery<T> query = this.factory.selectFrom(this.entity);
        if (null != predicate) {
            query = query.where(predicate);
        }
        return query.fetchCount();
    }

    /**
     * 分页
     *
     * @param query
     * @return
     */
    private JPAQuery<T> getPager(final JPAQuery<T> query) {
        JPAQuery<T> result = query;
        if (null != this.inquiry.getPager()) {
            final Pager pager = this.inquiry.getPager();
            Log.info(LOGGER, "[ UP ] [QE] Pagination: start = {0}, size = {1}", pager.getStart(), pager.getSize());
            result = result.limit(pager.getSize()).offset(pager.getStart());
        }
        return result;
    }

    /**
     * 排序
     *
     * @param query
     * @return
     */
    private JPAQuery<T> getOrderBy(final JPAQuery<T> query) {
        JPAQuery<T> result = query;
        if (null != this.inquiry.getSorter()) {
            final JsonObject sorter = this.inquiry.getSorter().toJson();
            final List<OrderSpecifier<?>> specifiers = new ArrayList<>();
            for (final String field : sorter.fieldNames()) {
                final Boolean isAsc = sorter.getBoolean(field);
                final Object path = Ut.field(this.entity, field);
                final OrderSpecifier<?> specifier = this.getOrderSpecifier(path, isAsc);
                if (null != specifier) {
                    specifiers.add(specifier);
                    Log.info(LOGGER, "[ UP ] [QE] Order By: field = {0}, asc = {1}", field, isAsc);
                }
            }
            result = result.orderBy(specifiers.toArray(new OrderSpecifier[]{}));
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    private <I extends Comparable> OrderSpecifier<I> getOrderSpecifier(final Object path, final boolean asc) {
        final Class<?> clazz = path.getClass();
        // StringPath类型的排序
        if (StringPath.class == clazz) {
            return asc ? (OrderSpecifier<I>) ((StringPath) path).asc() : (OrderSpecifier<I>) ((StringPath) path).desc();
        } else if (DateTimePath.class == clazz) {
            return asc ? (OrderSpecifier<I>) ((DateTimePath) path).asc() : (OrderSpecifier<I>) ((DateTimePath) path).desc();
        } else if (BooleanPath.class == clazz) {
            return asc ? (OrderSpecifier<I>) ((BooleanPath) path).asc() : (OrderSpecifier<I>) ((BooleanPath) path).desc();
        }
        return null;
    }
}
