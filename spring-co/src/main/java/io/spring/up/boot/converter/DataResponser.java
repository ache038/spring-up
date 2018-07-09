package io.spring.up.boot.converter;

import io.spring.up.epic.Ut;
import io.spring.up.epic.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DataResponser implements Responser {

    @Override
    public JsonObject process(final JsonObject original, final Object value) {
        final Iterable json = Ut.serializeJson(value);
        return Fn.getNull(new JsonObject().put("data", "null"), () -> {
            if (json instanceof JsonObject) {
                final JsonObject response = (JsonObject) json;
                return original.put("data", Ut.outKey(response));
            } else {
                final JsonArray response = Ut.outKey((JsonArray) json);
                return original.put("list", response).put("count", response.size());
            }
        }, original);
    }
}
