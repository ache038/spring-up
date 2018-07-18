package io.spring.up.ipc.core;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.spring.up.annotations.Ipc;
import io.spring.up.epic.Ut;
import io.spring.up.epic.fn.Fn;
import io.spring.up.log.Log;
import io.spring.up.model.Envelop;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IpcScanner extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(IpcScanner.class);
    private static final ConcurrentMap<String, Method> SCANNED
            = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Object> PROXIES
            = new ConcurrentHashMap<>();

    private transient final Class<?> target;
    private transient final Object reference;

    public IpcScanner(final Object reference) {
        this.reference = reference;
        this.target = reference.getClass();
        this.setName("ipc-scanner-" + this.getId());
    }

    public static ConcurrentMap<String, Method> getScanned() {
        return SCANNED;
    }

    public static Method getScanned(final String address) {
        return SCANNED.getOrDefault(address, null);
    }

    public static ConcurrentMap<String, Object> getProxies() {
        return PROXIES;
    }

    public static Object getProxies(final String address) {
        return PROXIES.getOrDefault(address, null);
    }

    @Override
    public void run() {
        Log.info(LOGGER, "[ UP ] Ipc Scanner started: {0} for {1}", this.getName(), this.target);
        Fn.safeNull(() -> Observable.fromArray(this.target.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(Ipc.class))
                        .filter(this::ensure)
                        .map(method -> Single.just(method)
                                .map(item -> item.getAnnotation(Ipc.class))
                                .map(item -> Ut.invoke(item, "value"))
                                .filter(Objects::nonNull)
                                .map(item -> (String) item)
                                .filter(item -> !Ut.isEmpty(item))
                                .map(this::put)
                                .subscribe(item -> SCANNED.put(item, method))
                        )
                        .subscribe(),
                this.target);
    }

    private String put(final String item) {
        PROXIES.put(item, this.reference);
        return item;
    }

    private boolean ensure(final Method method) {
        final Class<?> type = method.getReturnType();
        if (Envelop.class != type && JsonObject.class != type) {
            Log.warn(LOGGER, "[ UP ] Ignored the method {0} because of Return Type: {1}",
                    method.getName(), type.getName());
            return false;
        }
        final Parameter[] parameters = method.getParameters();
        if (1 != parameters.length) {
            Log.warn(LOGGER, "[ UP ] Ignored the method {0} because of Parameter Length must be 1, current = {1}",
                    method.getName(), parameters.length);
            return false;
        }
        final Parameter parameter = parameters[0];
        final Class<?> parameterType = parameter.getType();
        if (Envelop.class != parameterType && JsonObject.class != parameterType) {
            Log.warn(LOGGER, "[ UP ] Ignored the method {0} because of Parameter Type {1}",
                    method.getName(), parameterType.getName());
            return false;
        }
        return true;
    }
}
