package io.spring.up.query;

import io.spring.up.aiki.Ux;
import io.spring.up.exception.web._400QueryPagerInvalidException;
import io.spring.up.exception.web._500QueryMetaNullException;
import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;
import io.zero.epic.fn.Fn;

import java.io.Serializable;

public class Pager implements Serializable {

    private static final String PAGE = "page";
    private static final String SIZE = "size";
    /**
     * Start page: >= 1
     */
    private transient int page;
    /**
     * Page size
     */
    private transient int size;
    /**
     * From index: offset
     */
    private transient int start;
    /**
     * To index: limit
     */
    private transient int end;

    /**
     * Create pager by page, size
     *
     * @param page
     * @param size
     * @return
     */
    public static Pager create(final Integer page, final Integer size) {
        return new Pager(page, size);
    }

    /**
     * Another mode to create Pager
     *
     * @param pageJson
     * @return
     */
    public static Pager create(final JsonObject pageJson) {
        return new Pager(pageJson);
    }

    private void ensure(final JsonObject pageJson) {
        // Pager building checking
        Ux.out(null == pageJson,
                _500QueryMetaNullException.class, this.getClass());
        // Required
        Ux.out(!pageJson.containsKey(PAGE),
                _400QueryPagerInvalidException.class, this.getClass(), PAGE);
        Ux.out(!pageJson.containsKey(SIZE),
                _400QueryPagerInvalidException.class, this.getClass(), SIZE);
        // Types
        Inquiry.ensureType(pageJson, PAGE, Integer.class,
                Ut::isInteger, this.getClass());
        Inquiry.ensureType(pageJson, SIZE, Integer.class,
                Ut::isInteger, this.getClass());
    }

    private void init(final Integer page, final Integer size) {
        // Page/Size
        Ux.out(1 > page,
                _400QueryPagerInvalidException.class, this.getClass(), page);
        this.page = page;
        // Default Size is 10
        this.size = 0 < size ? size : 10;
        Fn.safeNull(() -> {
            // Caculate
            this.start = (this.page - 1) * this.size;
            this.end = this.page * this.size;
        }, this.page, this.size);
    }

    private Pager(final Integer page, final Integer size) {
        this.init(page, size);
    }

    private Pager(final JsonObject pageJson) {
        this.ensure(pageJson);
        this.init(pageJson.getInteger(PAGE), pageJson.getInteger(SIZE));
    }

    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        data.put(PAGE, this.page);
        data.put(SIZE, this.size);
        return data;
    }

    public int getPage() {
        return this.page;
    }

    public int getSize() {
        return this.size;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int getTop() {
        return this.size;
    }
}
