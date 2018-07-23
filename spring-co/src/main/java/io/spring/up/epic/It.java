package io.spring.up.epic;

import io.spring.up.epic.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("all")
class It {

    static <T> void itJArray(final JsonArray array,
                             final Class<T> clazz,
                             final BiConsumer<T, Integer> consumer) {
        Fn.safeNull(() -> {
            final int length = array.size();
            for (int idx = 0; idx < length; idx++) {
                final Object value = array.getValue(idx);
                if (null != value && clazz == value.getClass()) {
                    final T param = (T) value;
                    // 参数：值和索引
                    consumer.accept(param, idx);
                }
            }
        }, array, clazz);
    }

    static <V> void itList(final List<V> list,
                           final BiConsumer<V, Integer> fnEach) {
        final int size = list.size();
        for (int idx = 0; idx < size; idx++) {
            final V item = list.get(idx);
            if (null != item) {
                fnEach.accept(item, idx);
            }
        }
    }

    static void itJObject(final JsonObject object,
                          final BiConsumer<String, Object> consumer) {
        object.forEach(item -> {
            if (null != item) {
                final String key = item.getKey();
                final Object value = item.getValue();
                if (null != key && null != value) {
                    // 参数：键和值
                    consumer.accept(key, value);
                }
            }
        });
    }

    static <T> void itJObject(final JsonObject object,
                              final Class<T> clazz,
                              final BiConsumer<String, T> consumer) {
        object.forEach(item -> {
            if (null != item) {
                final String key = item.getKey();
                final Object value = item.getValue();
                if (null != key && null != value && clazz == value.getClass()) {
                    // 参数：键和值
                    final T param = (T) value;
                    consumer.accept(key, param);
                }
            }
        });
    }

    static <T> HashSet<T> rdcHashSet(final HashSet<T> collection, final T element) {
        collection.add(element);
        return collection;
    }
}
