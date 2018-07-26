package io.spring.up.query;

import io.spring.up.aiki.Ux;
import io.spring.up.exception.web._400QueryOpUnsupportException;
import io.spring.up.exception.web._500QueryMetaNullException;
import io.vertx.core.json.JsonObject;
import io.zero.epic.container.KeyPair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Criteria for condition set, the connector is and
 * Advanced criteria will use tree mode, the flat mode is "AND"
 */
public class Criteria implements Serializable {

    private final List<KeyPair<String, KeyPair<String, Object>>> conditions = new ArrayList<>();

    public static Criteria create(final JsonObject data) {
        return new Criteria(data);
    }

    private Criteria(final JsonObject data) {
        Ux.out(null == data,
                _500QueryMetaNullException.class, this.getClass());
        for (final String field : data.fieldNames()) {
            // Add
            this.add(field, data.getValue(field));
        }
    }

    public List<KeyPair<String, KeyPair<String, Object>>> getConditions() {
        return this.conditions;
    }

    public boolean isValid() {
        return !this.conditions.isEmpty();
    }

    public Criteria add(final String field, final Object value) {
        // Field add
        final String filterField;
        final String op;
        if (field.contains(",")) {
            filterField = field.split(",")[0];
            op = field.split(",")[1];
        } else {
            filterField = field;
            op = Inquiry.Op.EQ;
        }
        Ux.out(!Inquiry.Op.VALUES.contains(op),
                _400QueryOpUnsupportException.class, this.getClass(), op);
        final KeyPair<String, Object> condition = KeyPair.create(op, value);
        final KeyPair<String, KeyPair<String, Object>> item = KeyPair.create(filterField, condition);
        // At the same time.
        this.conditions.add(item);
        return this;
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        for (final KeyPair<String, KeyPair<String, Object>> item : this.conditions) {
            final String field = item.getKey();
            final KeyPair<String, Object> value = item.getValue();
            final String op = value.getKey();
            final Object hitted = value.getValue();
            json.put(field + "," + op, hitted);
        }
        return json;
    }
}
