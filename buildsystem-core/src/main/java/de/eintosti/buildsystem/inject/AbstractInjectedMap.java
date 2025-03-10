package de.eintosti.buildsystem.inject;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractInjectedMap<K, V> implements Map<K, V> {

    private final Map<K, V> origin;

    public AbstractInjectedMap(Map<K, V> origin) {
        this.origin = origin;
    }

    public abstract World onQuery(K key);

    @Override
    public int size() {
        return origin.size();
    }

    @Override
    public boolean isEmpty() {
        return origin.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return origin.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return origin.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (origin.containsKey(key)) {
            return origin.get(key);
        }
        return (V) onQuery((K) key);
    }

    @Override
    public @Nullable V put(K key, V value) {
        return origin.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return origin.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        origin.putAll(m);
    }

    @Override
    public void clear() {
        origin.clear();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return origin.keySet();
    }

    @Override
    public @NotNull Collection<V> values() {
        return origin.values();
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return origin.entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return origin.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        origin.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        origin.replaceAll(function);
    }

    @Override
    public @Nullable V putIfAbsent(K key, V value) {
        return origin.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return origin.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return origin.replace(key, oldValue, newValue);
    }

    @Override
    public @Nullable V replace(K key, V value) {
        return origin.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        return origin.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return origin.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, @NotNull BiFunction<? super K, ? super @Nullable V, ? extends V> remappingFunction) {
        return origin.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return origin.merge(key, value, remappingFunction);
    }

}
