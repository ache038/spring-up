package io.spring.up.query.cond;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import io.vertx.core.json.JsonArray;
import io.vertx.up.atom.query.Inquiry;

import java.util.ArrayList;
import java.util.List;

public class StringConsider implements Consider {

    private transient final StringPath path;

    StringConsider(final Object path) {
        this.path = (StringPath) path;
    }

    @Override
    @SuppressWarnings("all")
    public BooleanExpression operator(final String op, final Object value) {
        BooleanExpression predicate = null;
        switch (op) {
            case Inquiry.Op.EQ:
                predicate = path.eq(value.toString());
                break;
            case Inquiry.Op.START:
                predicate = path.like(value.toString() + "%");
                break;
            case Inquiry.Op.END:
                predicate = path.like("%" + value.toString());
                break;
            case Inquiry.Op.CONTAIN:
                predicate = path.like("%" + value.toString() + "%");
                break;
            case Inquiry.Op.IN: {
                if (null != value) {
                    List<String> arrays = new ArrayList<>();
                    if (value instanceof JsonArray) {
                        arrays = ((JsonArray) value).getList();
                    }
                    predicate = path.in(arrays);
                }
            }
            break;
            case Inquiry.Op.NOT_IN: {
                if (null != value) {
                    List<String> arrays = new ArrayList<>();
                    if (value instanceof JsonArray) {
                        arrays = ((JsonArray) value).getList();
                    }
                    predicate = path.notIn(arrays);
                }
            }
            break;
            case Inquiry.Op.NULL: {
                predicate = path.isNull();
            }
            break;
            case Inquiry.Op.NOT_NULL: {
                predicate = path.isNotNull();
            }
            break;
            default:
                predicate = path.eq(value.toString());
                break;
        }
        return predicate;
    }
}
