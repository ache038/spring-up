package io.spring.up.query;

import io.spring.up.epic.fn.Fn;
import io.spring.up.exception.web._400QueryParamTypeException;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 查询接口
 */
public interface Inquiry {

    // Criteria Keys
    String KEY_PAGER = "pager";
    String KEY_SORTER = "sorter";
    String KEY_CRITERIA = "criteria";
    String KEY_PROJECTION = "projection";

    static Inquiry create(final JsonObject data) {
        return new IrInquiry(data);
    }

    static void ensureType(final JsonObject checkJson,
                           final String key, final Class<?> type,
                           final Predicate<Object> predicate,
                           final Class<?> target) {
        Fn.safeNull(() -> {
            if (checkJson.containsKey(key)) {
                final Object check = checkJson.getValue(key);
                Fn.out(!predicate.test(check), _400QueryParamTypeException.class, target,
                        key, type, check.getClass());
            }
        }, target, checkJson);
    }

    /**
     * Get projection
     *
     * @return Projection to do filter
     */
    Set<String> getProjection();

    /**
     * Get pager
     *
     * @return Pager for pagination
     */
    Pager getPager();

    /**
     * Get Sorter
     *
     * @return Sorter for order by
     */
    Sorter getSorter();

    /**
     * Get criteria
     *
     * @return
     */
    Criteria getCriteria();

    void setInquiry(String field, Object value);

    /**
     * To JsonObject
     *
     * @return
     */
    JsonObject toJson();

    interface Op {
        String LT = "<";
        String LE = "<=";
        String GT = ">";
        String GE = ">=";
        String EQ = "=";
        String NEQ = "<>";
        String NOT_NULL = "!n";
        String NULL = "n";
        String TRUE = "t";
        String FALSE = "f";
        String IN = "i";
        String NOT_IN = "!i";
        String START = "s";
        String END = "e";
        String CONTAIN = "c";

        Set<String> VALUES = new HashSet<String>() {
            {
                this.add(LT);
                this.add(LE);
                this.add(GT);
                this.add(GE);
                this.add(EQ);
                this.add(NEQ);
                this.add(NOT_NULL);
                this.add(NULL);
                this.add(TRUE);
                this.add(FALSE);
                this.add(IN);
                this.add(NOT_IN);
                this.add(START);
                this.add(END);
                this.add(CONTAIN);
            }
        };
    }
}
