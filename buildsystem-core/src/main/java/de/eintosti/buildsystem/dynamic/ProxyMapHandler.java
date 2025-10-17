package de.eintosti.buildsystem.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Function;

public record ProxyMapHandler<K, V>(Map<K, V> originalMap, Function<K, V> queryFunction) implements InvocationHandler {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> createInjectedMap(Map<K, V> originalMap, Function<K, V> queryFunction) {
        return (Map<K, V>) Proxy.newProxyInstance(
            originalMap.getClass().getClassLoader(),
            new Class<?>[]{Map.class},
            new ProxyMapHandler<>(originalMap, queryFunction)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("get".equals(method.getName()) && args.length == 1) {
            K key = (K) args[0];
            if (!originalMap.containsKey(key)) {
                return queryFunction.apply(key);
            }
        }
        return method.invoke(originalMap, args);
    }
}