package com.application.eurder_project.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class FakeDatabase<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<UUID, T> storedItems = new ConcurrentHashMap<>();
    private final Function<T, UUID> idOf;

    protected FakeDatabase(Function<T, UUID> idOf) {
        this.idOf = idOf;
    }

    public List<T> getAll() {
        logger.trace("Fetching all items ({} stored).", storedItems.size());
        return storedItems.values().stream().toList();
    }

    public Optional<T> get(UUID id) {
        logger.trace("Fetching item {}.", id);
        return Optional.ofNullable(storedItems.get(id));
    }

    public T save(T item) {
        UUID id = idOf.apply(item);
        logger.trace("Saving item {}.", id);
        storedItems.put(id, item);
        return item;
    }

    protected List<T> filter(Predicate<T> predicate) {
        return storedItems.values().stream().filter(predicate).toList();
    }

    // Test only: removes a single item.
    public T deleteWithId(UUID id, T item) {
        logger.trace("Deleting item {}.", id);
        storedItems.remove(id, item);
        return item;
    }

    // Test only: empties the table so tests do not influence each other.
    public void reset() {
        logger.debug("Reset table");
        storedItems.clear();
    }
}
