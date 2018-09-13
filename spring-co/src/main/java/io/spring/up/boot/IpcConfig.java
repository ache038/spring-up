package io.spring.up.boot;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.spring.up.aiki.Ux;
import io.spring.up.annotations.Ipc;
import io.spring.up.ipc.core.IpcScanner;
import io.spring.up.ipc.core.IpcSelector;
import io.spring.up.log.Log;
import io.zero.epic.fn.Fn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Configuration
public class IpcConfig implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpcConfig.class);
    // 后边放到配置文件中
    private static final String[] IGNORES = new String[]{
            "org.springframework",
            "com.netflix",
            "org.apache",
            "springfox",
            "com.hazelcast",
            "com.sun",
            "com.fasterxml",
            "org.zalando",
            "com.ryantenney",
            "net",
            "io",
            "java",
            "javax",
            "feign",
            "org.thymeleaf",
            "com.google",
            "com.zaxxer"
    };

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        final ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        final String[] names = context.getBeanDefinitionNames();
        final Set<Object> objects = Observable.fromArray(names)
                .map(context::getBean)
                .filter(Objects::nonNull)
                .filter(reference -> this.isValid(reference.getClass()))
                .reduce(new HashSet<>(), Ux::reduceSet)
                .blockingGet();
        if (!objects.isEmpty()) {
            // 线程变量控制，筛选合法的方法进行扫描
            Log.info(LOGGER, "[ IPC ] Selected around {0} classes that will be scanned.", String.valueOf(objects.size()));
            final CountDownLatch counter = new CountDownLatch(objects.size());
            Observable.fromIterable(objects)
                    .map(reference -> Schedulers.computation().scheduleDirect(new IpcScanner(reference, counter)))
                    .subscribe();
            Fn.safeJvm(() -> {
                counter.await();
                IpcScanner.getScanned().forEach((key, value) ->
                        Log.info(LOGGER, "\t\t[ IPC ] Address = {0}, Scanned method \"{1} {2}({3})\".", key,
                                value.getReturnType().getName(), value.getName(), value.getParameterTypes()[0].getName()));
            });
        }
    }

    private synchronized boolean isValid(final Class<?> clazz) {
        final Set<String> sets = new HashSet<>();
        for (final String prefix : IGNORES) {
            if (clazz.getName().startsWith(prefix)
                    || 0 < clazz.getName().indexOf('$')) {
                sets.add(clazz.getName());
                break;
            }
            if (!clazz.isAnnotationPresent(Component.class)) {
                sets.add(clazz.getName());
                break;
            }
            final Method[] methods = clazz.getDeclaredMethods();
            final Set<Method> methodSet = Arrays.stream(methods)
                    .filter(item -> item.isAnnotationPresent(Ipc.class))
                    .filter(IpcSelector::filterMethod)
                    .collect(Collectors.toSet());
            if (methodSet.isEmpty()) {
                sets.add(clazz.getName());
                break;
            }
        }
        return sets.isEmpty();
    }
}
