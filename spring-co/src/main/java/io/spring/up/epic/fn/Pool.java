package io.spring.up.epic.fn;

import io.spring.up.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

class Pool {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pool.class);

    static <K, V> V exec(final ConcurrentMap<K, V> pool,
                         final K key,
                         final Supplier<V> poolFn) {
        if (null == pool || null == key) {
            Log.upw(LOGGER, "Pool.exec detect null args: pool = {0}, key = {1}", pool, key);
            return null;
        }
        V reference = pool.get(key);
        if (null == reference) {
            reference = poolFn.get();
            if (null != reference) {
                pool.put(key, reference);
            }
        }
        return reference;
    }
}
