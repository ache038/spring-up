package io.spring.up.aiki;

import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;
import org.junit.Test;

public class ConvertTest {

    @Test
    public void testConvert() {
        final JsonObject data = Ut.ioJObject("test/data.json");
        final JsonObject converted = Ux.outKey(data);
        System.out.println(converted.encodePrettily());
    }

    @Test
    public void testConvert1() {
        final JsonObject data = Ut.ioJObject("test/in.json");
        final JsonObject converted = Ux.inKey(data);
        System.out.println(converted.encodePrettily());
    }
}
