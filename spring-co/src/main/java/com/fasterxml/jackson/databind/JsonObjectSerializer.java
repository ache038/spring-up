package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import io.spring.up.log.Log;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonObjectSerializer extends JsonSerializer<JsonObject> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectSerializer.class);

    @Override
    public void serialize(final JsonObject value,
                          final JsonGenerator jgen,
                          final SerializerProvider provider) throws IOException {
        final JsonObject processed = null == value ? new JsonObject() : value;
        Log.updg(LOGGER, "JsonObject Serializer monitor: {0}, data = {1}", value, processed.encode());
        jgen.writeObject(processed.getMap());
    }
}