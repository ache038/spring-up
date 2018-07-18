package io.spring.up.epic;

import io.spring.up.log.Log;
import io.vertx.core.json.JsonObject;
import org.apache.commons.jexl3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Expr {

    private static final JexlEngine EXPR = new JexlBuilder()
            .cache(512).silent(false).create();

    private static final Logger LOGGER = LoggerFactory.getLogger(Expr.class);

    static String expression(final String expr, final JsonObject params) {
        try {
            final JexlExpression expression = EXPR.createExpression(expr);
            // Parameter
            final JexlContext context = new MapContext();
            Ut.itJObject(params, context::set);
            return expression.evaluate(context).toString();
        } catch (final JexlException ex) {
            Log.jvm(LOGGER, ex);
            throw ex;
        }
    }

    static String uri(final String url) {
        if (null == url) {
            return null;
        }
        if (url.contains("?")) {
            return url.split("\\?")[0];
        } else {
            return url;
        }
    }
}
