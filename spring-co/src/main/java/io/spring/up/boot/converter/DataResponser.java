package io.spring.up.boot.converter;

import io.spring.up.tool.Ut;
import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonObject;

public class DataResponser implements Responser {

    @Override
    public JsonObject process(final JsonObject original, final Object value) {
        final JsonObject json = Ut.serializeJson(value);
        return Fn.getNull(new JsonObject().put("data", "null"), () -> original.put("data", json), original);
    }
}
