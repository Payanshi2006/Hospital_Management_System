package repository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryRepository<K, V> {
    private final ConcurrentMap<K, V> records = new ConcurrentHashMap<>();

    public final boolean add(K key, V value) {
        return records.putIfAbsent(
                Objects.requireNonNull(key, "Repository key cannot be null."),
                Objects.requireNonNull(value, "Repository value cannot be null.")) == null;
    }

    public final V findByKey(K key) {
        return records.get(Objects.requireNonNull(key, "Repository key cannot be null."));
    }

    public final List<V> findAll() {
        return List.copyOf(records.values());
    }
}
