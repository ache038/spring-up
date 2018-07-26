package io.spring.up.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("all")
public class IrInquiry implements Inquiry {

    private transient Pager pager;
    private transient Sorter sorter;
    private transient Set<String> projection;
    private transient Criteria criteria;

    IrInquiry(final JsonObject input) {

        this.ensure(input);

        this.init(input);
    }

    private void init(final JsonObject input) {
        if (input.containsKey(KEY_PAGER)) {
            this.pager = Pager.create(input.getJsonObject(KEY_PAGER));
        }
        if (input.containsKey(KEY_SORTER)) {
            this.sorter = Sorter.create(input.getJsonArray(KEY_SORTER));
        }
        if (input.containsKey(KEY_PROJECTION)) {
            this.projection = new HashSet<String>(input.getJsonArray(KEY_PROJECTION).getList());
        }
        if (input.containsKey(KEY_CRITERIA)) {
            this.criteria = Criteria.create(input.getJsonObject(KEY_CRITERIA));
        }
    }

    private void ensure(final JsonObject input) {
        // Sorter checking
        Inquiry.ensureType(input, KEY_SORTER, JsonArray.class,
                Ut::isJArray, this.getClass());
        // Projection checking
        Inquiry.ensureType(input, KEY_PROJECTION, JsonArray.class,
                Ut::isJArray, this.getClass());
        // Pager checking
        Inquiry.ensureType(input, KEY_PAGER, JsonObject.class,
                Ut::isJObject, this.getClass());
        // Criteria
        Inquiry.ensureType(input, KEY_CRITERIA, JsonObject.class,
                Ut::isJObject, this.getClass());

    }

    @Override
    public Set<String> getProjection() {
        return this.projection;
    }

    @Override
    public Pager getPager() {
        return this.pager;
    }

    @Override
    public Sorter getSorter() {
        return this.sorter;
    }

    @Override
    public Criteria getCriteria() {
        return this.criteria;
    }

    @Override
    public void setInquiry(final String field, final Object value) {
        if (null == this.criteria) {
            this.criteria = Criteria.create(new JsonObject());
        }
        this.criteria.add(field, value);
    }

    @Override
    public JsonObject toJson() {
        final JsonObject result = new JsonObject();
        if (null != this.pager) {
            result.put(KEY_PAGER, this.pager.toJson());
        }
        if (null != this.sorter) {
            result.put(KEY_SORTER, this.sorter.toJson());
        }
        if (null != this.projection) {
            result.put(KEY_PROJECTION, new JsonArray(new ArrayList(this.projection)));
        }
        if (null != this.criteria) {
            result.put(KEY_CRITERIA, this.criteria.toJson());
        }
        return result;
    }
}
