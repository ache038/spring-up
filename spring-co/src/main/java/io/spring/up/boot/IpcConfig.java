package io.spring.up.boot;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.spring.up.epic.Ut;
import io.spring.up.ipc.core.IpcScanner;
import io.spring.up.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
            "com.google"
    };

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        final ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        final String[] names = context.getBeanDefinitionNames();
        Observable.fromArray(names)
                .map(context::getBean)
                .filter(Objects::nonNull)
                .filter(reference -> this.isValid(reference.getClass()))
                .map(Ut::rxDebug)
                .map(reference -> Schedulers.io().scheduleDirect(new IpcScanner(reference)))
                .subscribe();
        IpcScanner.getScanned().forEach((key, value) ->
                Log.info(LOGGER, "\t\t[ IPC ] Address = {0}, Scanned method \"{1} {2}({3})\".", key,
                        value.getReturnType().getName(), value.getName(), value.getParameterTypes()[0].getName()));
    }

    private boolean isValid(final Class<?> clazz) {
        final Set<String> sets = Observable.fromArray(IGNORES)
                .filter(item -> clazz.getName().startsWith(item))
                // 过滤动态代理类和特殊类
                .filter(item -> 0 <= clazz.getName().indexOf('$'))
                .reduce(new HashSet<String>(), (set, element) -> {
                    set.add(element);
                    return set;
                }).blockingGet();
        return sets.isEmpty() && !clazz.isAnonymousClass() && !clazz.isInterface() && !clazz.isAnnotation();
    }
}
