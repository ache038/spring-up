package io.spring.up.epic;

import io.spring.up.epic.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

class To {

    static <T extends Enum<T>> T toEnum(final Class<T> clazz, final String input) {
        return Fn.getJvm(null, () -> Enum.valueOf(clazz, input), clazz, input);
    }

    static Integer toInteger(final Object value) {
        return Fn.getNull(null, () -> Integer.parseInt(value.toString()), value);
    }

    static String toString(final Object value) {
        return Fn.getNull("", () -> {
            if (value instanceof JsonObject) {
                return ((JsonObject) value).encode();
            }
            if (value instanceof JsonArray) {
                return ((JsonArray) value).encode();
            }
            return value.toString();
        }, value);
    }
}
