package io.zero.epic;

import io.reactivex.Single;
import io.spring.up.exception.WebException;
import io.spring.up.exception.web._500FunctionExecuteException;
import io.spring.up.exception.web._500URIInvalidException;
import io.spring.up.log.Log;
import io.zero.epic.fn.JvmSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.function.Function;
import java.util.function.Supplier;

class Async {
    private static final Logger LOGGER = LoggerFactory.getLogger(Async.class);

    static <T> Single<ResponseEntity<T>> ok(final Single<T> item) {
        return Single.just(ResponseEntity.ok(item.blockingGet()));
    }

    static <T> Single<ResponseEntity<T>> ok(final Supplier<T> supplierFun) {
        return execute(() -> ResponseEntity.ok(supplierFun.get()), null);
    }

    static <T> Function<T, Single<ResponseEntity<T>>> ok(final Function<T, T> applyFun) {
        return item -> execute(() -> ResponseEntity.ok(applyFun.apply(item)), null);
    }

    static <T> Function<T, Single<T>> start(final Function<T, T> applyFun) {
        return item -> execute(() -> applyFun.apply(item), null);
    }

    static <T> Function<String, Single<ResponseEntity<T>>> created(final Single<T> item) {
        return url -> execute(() -> {
            final T entity = item.blockingGet();
            final URI uri = new URI(url);
            return ResponseEntity.created(uri).body(entity);
        }, _500URIInvalidException.class, Async.class, url);
    }

    private static <T> Single<T> execute(final JvmSupplier<T> supplierFun,
                                         final Class<?> clazz,
                                         final Object... args) {
        try {
            final T entity = supplierFun.get();
            return Single.just(entity);
        } catch (final Throwable ex) {
            Log.jvm(LOGGER, ex);
            if (LOGGER.isDebugEnabled()) {
                ex.printStackTrace();
            }
            final WebException error;
            if (null == clazz) {
                error = new _500FunctionExecuteException(Async.class, ex);
            } else {
                error = Utt.instance(clazz, args);
            }
            return Single.error(error);
        }
    }
}