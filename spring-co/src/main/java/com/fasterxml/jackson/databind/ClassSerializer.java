package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

/**
 * @author Lang
 */
public class ClassSerializer extends JsonSerializer<Class<?>> { // NOPMD


    /**
     *
     */
    @Override
    public void serialize(final Class<?> clazz, final JsonGenerator jgen,
                          final SerializerProvider provider)
            throws IOException {
        jgen.writeString(clazz.getName());
        jgen.flush();
        jgen.close();
    }
}
