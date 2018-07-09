package io.spring.up.epic;

import io.spring.up.epic.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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
            It.itJArray(input, JsonObject.class, (item, index) -> {
                converted.add(convert(item, from, to));
            });
            return converted;
        }, input, from, to);
    }
}
