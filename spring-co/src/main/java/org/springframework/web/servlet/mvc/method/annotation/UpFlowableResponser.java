package org.springframework.web.servlet.mvc.method.annotation;

import io.spring.up.boot.converter.Responser;
import io.spring.up.model.Pagination;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.zero.eon.Values;

public class UpFlowableResponser implements Responser {

    public static Class<?> FLOWABLE_CLS = ReactiveTypeHandler.CollectedValuesList.class;

    @Override
    public JsonObject process(final Object pagination) {
        // 过滤null元素
        final ReactiveTypeHandler.CollectedValuesList valuesList =
                (ReactiveTypeHandler.CollectedValuesList) pagination;
        // 查看该集合的相关信息
        if (0 == valuesList.size()) {
            // 没有任何元素
            return new JsonObject().put("count", 0).put("list", new JsonArray());
        } else {
            if (valuesList.getReturnType().getComponentType().isAssignableFrom(Pagination.class)) {
                // Pagination特殊处理
                return Responser.get(Pagination.class).process(valuesList.get(Values.IDX));
            } else {
                // 其他照旧
                return Responser.get(Object.class).process(valuesList);
            }
        }
    }
}
