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
}
