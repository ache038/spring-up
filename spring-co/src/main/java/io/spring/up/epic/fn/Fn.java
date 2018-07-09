package io.spring.up.epic.fn;

import io.spring.up.epic.Ut;
import io.spring.up.exception.WebException;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * 内部使用函数包
 */
public final class Fn {

    public static <T> T getNull(
            final T defaultValue,
            final Supplier<T> supplier,
            final Object... reference) {
        return Null.get(defaultValue, supplier, reference);
    }

    public static <T> T getNull(
            final Supplier<T> supplier,
            final Object... reference) {
        return Null.get(null, supplier, reference);
    }

    public static void safeNull(
            final Actuator actuator,
            final Object... reference) {
        Null.execute(actuator, reference);
    }

    public static <T> T getJvm(
            final T defaultValue,
            final JvmSupplier<T> supplier,
            final Object... reference) {
        return Jvm.get(defaultValue, supplier, reference);
    }

    public static <T> T getJvm(
            final JvmSupplier<T> supplier,
            final Object... reference) {
        return Jvm.get(null, supplier, reference);
    }

    public static void safeJvm(
            final JvmActuator actuator,
            final Object... reference) {
        Jvm.execute(actuator, reference);
    }

    public static <K, V> V pool(
            final ConcurrentMap<K, V> pool,
            final K key,
            final Supplier<V> poolFn) {
        return Pool.exec(pool, key, poolFn);
    }

    public static void out(final Supplier<Boolean> condFun,
                           final Class<?> clazz, final Object... args) {
        final Boolean checked = condFun.get();
        out(checked, clazz, args);
    }

    public static void out(final Boolean condFun,
                           final Class<?> clazz, final Object... args) {
        if (condFun) {
            throw (WebException) Ut.instance(clazz, args);
        }
    }
}
