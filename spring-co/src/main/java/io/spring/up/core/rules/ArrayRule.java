package io.spring.up.core.rules;

import io.spring.up.epic.Ut;
import io.spring.up.exception.WebException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ArrayRule implements Rule {

    @Override
    public WebException verify(final String field,
                               final Object value,
                               final JsonObject config) {
        return Rule.verify(this.getClass(), () -> {
            if (null == value) {
                return false;
            }
            final String literal = value.toString();
            if (Ut.isJArray(literal)) {
                final JsonArray array = new JsonArray(literal);
                if (config.containsKey("minlength")) {
                    final Integer minlength = config.getInteger("minlength");
                    return minlength <= array.size();
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }, field, value, "array", config);
    }
}
