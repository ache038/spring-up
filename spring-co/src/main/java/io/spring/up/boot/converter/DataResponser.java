package io.spring.up.boot.converter;

import io.spring.up.aiki.Ux;
import io.spring.up.log.Log;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;
import io.zero.epic.fn.Fn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataResponser implements Responser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataResponser.class);

    @Override
    public JsonObject process(final JsonObject original, final Object value) {
        Log.updg(LOGGER, "Value before serialization type = {1}, literal = {0}, original = {2}",
                value,
                null != value ? value.getClass() : null,
                original);
        final Iterable json = Ut.serializeJson(value);
        Log.updg(LOGGER, "Value after serialization: {0}", json);
        return Fn.getNull(new JsonObject().put("data", "null"), () -> {
            if (json instanceof JsonObject) {
                final JsonObject response = (JsonObject) json;
                return original.copy().put("data", Ux.outKey(response));
            } else {
                final JsonArray response = Ux.outKey((JsonArray) json);
                return original.copy().put("list", response).put("count", response.size());
            }
        }, original);
    }
}
