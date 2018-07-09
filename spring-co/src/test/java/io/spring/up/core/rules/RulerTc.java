package io.spring.up.core.rules;

import io.spring.up.epic.Ut;
import io.spring.up.exception.web._400ValidationException;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.jar.JarInputStream;

public class RulerTc {

    @Test(expected = _400ValidationException.class)
    public void testRequired() {
        final JsonObject data = new JsonObject().put("username", "admin");
        Ruler.verify("enterprise", data);
    }

    @Test
    public void testStream() throws Exception {
        this.testInput("/Users/lang/.m2/repository/cn/spring-up/spring-co/0.2/spring-co-0.2.jar!/internal/application-error.yml");

    }

    private void testInput(final String file) throws Exception {
        Ut.ioStream("internal/application-error.yml");
        new FileInputStream("/Users/lang/.m2/repository/cn/spring-up/spring-co/0.2/spring-co-0.2.jar");
        final JarInputStream in = new JarInputStream(new FileInputStream(file));
        System.out.println(in);
    }
}
