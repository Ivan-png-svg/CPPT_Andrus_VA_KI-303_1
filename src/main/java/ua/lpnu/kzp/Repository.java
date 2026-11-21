package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/** Type-safe in-memory repository with immutable snapshots. */
public final class Repository<T> {
    private final List<T> items = new ArrayList<>();
    /** Adds a non-null record. */
    public void add(T item) { items.add(Objects.requireNonNull(item)); }
    /** Returns an immutable snapshot. */
    public List<T> all() { return List.copyOf(items); }
    /** Returns matching records. */
    public List<T> find(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return items.stream().filter(predicate).toList();
    }
}
