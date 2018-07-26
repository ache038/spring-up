package io.spring.up.aiki;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;
import io.zero.epic.fn.Fn;

class Json {

    static JsonObject convert(final JsonObject input, final String from, final String to) {
        return Fn.getJvm(new JsonObject(), () -> {
            if (input.containsKey(from)) {
                input.put(to, input.getValue(from));
                input.remove(from);
            }
            return input;
        }, input, from, to);
    }

    static JsonArray convert(final JsonArray input, final String from, final String to) {
        return Fn.getJvm(new JsonArray(), () -> {
            final JsonArray converted = new JsonArray();
            Ut.itJArray(input, JsonObject.class, (item, index) -> {
                converted.add(convert(item, from, to));
            });
            return converted;
        }, input, from, to);
    }
}
