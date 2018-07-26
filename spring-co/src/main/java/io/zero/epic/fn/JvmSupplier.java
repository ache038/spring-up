package io.zero.epic.fn;

@FunctionalInterface
public interface JvmSupplier<T> {

    T get() throws Exception;
}
