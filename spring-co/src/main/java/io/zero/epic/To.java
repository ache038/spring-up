package io.zero.epic;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero.epic.fn.Fn;

import java.util.Map;

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

    static JsonObject toJObject(final Map<String, Object> map) {
        final JsonObject params = new JsonObject();
        Fn.safeNull(() -> map.forEach(params::put), map);
        return params;
    }
}
