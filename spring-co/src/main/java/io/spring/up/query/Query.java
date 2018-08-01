package io.spring.up.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.spring.up.cv.Strings;
import io.spring.up.log.Log;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.up.atom.query.Inquiry;
import io.vertx.up.atom.query.Pager;
import io.vertx.zero.eon.Values;
import io.zero.epic.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Query<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Query.class);

    private final transient Inquiry inquiry;
    private transient EntityPathBase<T> entity;
    private transient JPAQueryFactory factory;

    private Query(final JsonObject input) {
        this.inquiry = Inquiry.create(input);
    }

    public static <I> Query<I> create(final JsonObject input) {
        return new Query<>(input);
    }

    public Query<T> on(final EntityPathBase<T> entity) {
        this.entity = entity;
        return this;
    }

    public Query<T> on(final JPAQueryFactory factory) {
        this.factory = factory;
        return this;
    }

    public JsonObject searchFull() {
        final List<T> entities = this.searchAdvanced();
        final Long count = this.countAvanced();
        final JsonObject result = new JsonObject();
        final JsonArray listData = Ut.serializeJson(entities);
        result.put("list", listData);
        result.put("count", count);
        return result;
    }

    public List<T> searchAdvanced() {
        final Predicate predicate = this.getPredicate();
        JPAQuery<T> query = this.factory.selectFrom(this.entity);
        Log.warn(LOGGER, "[ UP ] [QE] Criteria = {0}", null == predicate ? null : predicate.toString());
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

    public Long countAvanced() {
        final Predicate predicate = this.getPredicate();
        JPAQuery<T> query = this.factory.selectFrom(this.entity);
        if (null != predicate) {
            query = query.where(predicate);
        }
        return query.fetchCount();
    }

    public Query<T> debug() {
        System.out.println(this.inquiry.getCriteria());
        System.out.println(this.inquiry.getPager());
        System.out.println(this.inquiry.getSorter());
        System.out.println(this.inquiry.getProjection());
        return this;
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
            Log.debug(LOGGER, "[ UP ] [QE] Pagination: start = {0}, size = {1}", pager.getStart(), pager.getSize());
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
                specifiers.add(specifier);
                Log.debug(LOGGER, "[ UP ] [QE] Order By: field = {0}, asc = {1}", field, isAsc);
            }
            result = result.orderBy(specifiers.toArray(new OrderSpecifier[]{}));
        }
        return result;
    }

    /**
     * 递归计算
     *
     * @return
     */
    private Predicate getPredicate() {
        BooleanExpression resultPredicate = null;
        if (null != this.inquiry.getCriteria()) {
            final JsonObject criteria = this.inquiry.getCriteria().toJson().copy();
            if (Inquiry.Mode.LINEAR == this.inquiry.getCriteria().getMode()) {
                // 线性搜索
                resultPredicate = this.getLinearPredicate(criteria);
            } else {
                // 查询树的搜索
                resultPredicate = this.getTreePredicate(criteria);
            }
        }
        return resultPredicate;
    }

    private BooleanExpression getTreePredicate(final JsonObject criteria) {
        BooleanExpression resultPredicate = null;
        // 计算链接符
        final Inquiry.Connector connector = this.getConnector(criteria);
        // 抽取当前路径中的线性条件
        final JsonObject linear = new JsonObject();
        criteria.remove(Strings.EMPTY);
        // 设置其他子树条件
        List<BooleanExpression> treeExpr = new ArrayList<>();
        for (final String field : criteria.fieldNames()) {
            final Object value = criteria.getValue(field);
            if (!Ut.isJObject(value)) {
                linear.put(field, value);
            } else {
                treeExpr.add(this.getTreePredicate(criteria.getJsonObject(field)));
            }
        }
        // 创建线性条件
        treeExpr.add(this.getLinearPredicate(linear));
        treeExpr = treeExpr.stream().filter(Objects::nonNull).collect(Collectors.toList());
        // 如果只有一个元素
        if (!treeExpr.isEmpty()) {
            resultPredicate = treeExpr.get(Values.IDX);
            for (int idx = Values.ONE; idx < treeExpr.size(); idx++) {
                final BooleanExpression next = treeExpr.get(idx);
                // 递归组合查询条件
                resultPredicate = this.mergePredicate(connector, resultPredicate, next);
            }
        }
        return resultPredicate;
    }

    private BooleanExpression getLinearPredicate(final JsonObject criteria) {
        BooleanExpression resultPredicate = null;
        // 连接符运算
        final Inquiry.Connector connector = this.getConnector(criteria);
        Log.debug(LOGGER, "[ UP ] [QE] Connector = {0}", connector);
        for (final String field : criteria.fieldNames()) {
            final String op = this.getKey(field);
            final String targetField = field.split(",")[0];
            // 包含子查询
            final BooleanExpression pre;
            final Object value = criteria.getValue(field);
            // 计算路径：只支持二级
            final Object path;
            if (targetField.contains(".")) {
                // 嵌套属性路径计算
                final String entity = targetField.substring(0, targetField.indexOf('.'));
                final String entityField = targetField.substring(targetField.indexOf('.') + 1);
                path = this.calcPath(this.entity, entity, entityField);
            } else {
                // 直接属性路径计算
                path = Ut.field(this.entity, targetField);
            }
            // 等于null则略过
            if (null == path) {
                Log.debug(LOGGER, "[ UP ] [QE] Field = {0} does not exist in entity = {1}", targetField, this.entity);
            } else {
                pre = this.getPredicate(path, op).apply(value);
                // 递归组合查询条件
                resultPredicate = this.mergePredicate(connector, resultPredicate, pre);
            }
        }
        return resultPredicate;
    }

    private BooleanExpression mergePredicate(final Inquiry.Connector connector, final BooleanExpression left, final BooleanExpression right) {
        if (null == left || null == right) {
            if (null != left) {
                return left;
            }
            if (null != right) {
                return right;
            }
        }
        if (Inquiry.Connector.AND == connector) {
            return Expressions.allOf(left, right);
        } else {
            return Expressions.anyOf(left, right);
        }
    }

    /**
     * 计算当前级别的连接符：And或者Or
     *
     * @param data
     * @return
     */
    private Inquiry.Connector getConnector(final JsonObject data) {
        if (!data.containsKey(Strings.EMPTY)) {
            return Inquiry.Connector.OR;
        } else {
            final Boolean isAnd = Boolean.valueOf(data.getValue(Strings.EMPTY).toString());
            if (isAnd) {
                return Inquiry.Connector.AND;
            } else {
                return Inquiry.Connector.OR;
            }
        }
    }

    /**
     * 计算嵌套路径
     *
     * @param entity
     * @param entityField
     * @param field
     * @return
     */
    private Object calcPath(final Object entity, final String entityField, final String field) {
        final Object path = Ut.field(entity, entityField);
        return Ut.field(path, field);
    }

    /**
     * 计算当前的操作符，默认是eq
     *
     * @param field
     * @return
     */
    private String getKey(final String field) {
        if (field.contains(",")) {
            final String opStr = field.split(",")[1];
            if (Ut.isNil(opStr)) {
                return Inquiry.Op.EQ;
            } else {
                return opStr.trim().toLowerCase();
            }
        } else {
            return Inquiry.Op.EQ;
        }
    }

    private Function<Object, BooleanExpression> getPredicate(final Object path, final String op) {
        return value -> {
            BooleanExpression predicate = null;
            final Class<?> clazz = path.getClass();
            if (StringPath.class == clazz) {
                // StringPath类型
                predicate = this.dispatchByOp((StringPath) path, op, value);
            } else if (BooleanPath.class == clazz) {
                // BooleanPath类型
                predicate = ((BooleanPath) path).eq(Boolean.valueOf(value.toString()));
            }
            return predicate;
        };
    }

    @SuppressWarnings("unchecked")
    private <I extends Comparable> OrderSpecifier<I> getOrderSpecifier(final Object path, final boolean asc) {
        final Class<?> clazz = path.getClass();
        // StringPath类型的排序
        if (StringPath.class == clazz) {
            return asc ? (OrderSpecifier<I>) ((StringPath) path).asc() : (OrderSpecifier<I>) ((StringPath) path).desc();
        }
        return null;
    }

    private BooleanExpression dispatchByOp(final StringPath path, final String op, final Object value) {
        BooleanExpression predicate = null;
        switch (op) {
            case Inquiry.Op.EQ:
                predicate = path.eq(value.toString());
                break;
            case Inquiry.Op.START:
                predicate = path.like(value.toString() + "%");
                break;
            case Inquiry.Op.END:
                predicate = path.like("%" + value.toString());
                break;
            case Inquiry.Op.CONTAIN:
                predicate = path.like("%" + value.toString() + "%");
                break;
        }
        return predicate;
    }
}
