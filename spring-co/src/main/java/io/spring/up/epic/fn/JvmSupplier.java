package io.spring.up.epic.fn;

@FunctionalInterface
public interface JvmSupplier<T> {

    T get() throws Exception;
}
