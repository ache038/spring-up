package io.spring.up.query;

import io.vertx.core.json.JsonObject;
import io.vertx.up.atom.query.Inquiry;
import io.vertx.up.atom.query.Sorter;
import io.zero.epic.Ut;
import org.junit.Test;

public class CriteriaTest {

    @Test
    public void testReq1() {
        final JsonObject params = Ut.ioJObject("test/model-criteria.json");
        final JsonObject data = params.getJsonObject("req1");
        final Sorter sorter = Sorter.create(data.getJsonArray("sorter"));
        System.out.println(sorter.toJson());
        final Inquiry inquiry = Inquiry.create(data);
        Query.create(inquiry.toJson()).debug();
    }
}
