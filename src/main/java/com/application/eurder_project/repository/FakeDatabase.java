package com.application.eurder_project.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class FakeDatabase<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<UUID, T> storedItems = new ConcurrentHashMap<>();

    public List<T> getAll() {
        logger.trace(
                "Fetching all items ({} {} stored)",
                storedItems.size(),
                storedItems.getClass()
        );
        return storedItems.values().stream().toList();
    }

    public Optional<T> get(UUID id) {
        logger.trace("Fetching item {} of the class {}", id, storedItems.getClass());
        return Optional.ofNullable(storedItems.get(id));
    }

    protected T saveWithId(UUID id, T item) {
        logger.debug("Saving item {} in {} repository", id, item.getClass());
        storedItems.put(id, item);
        return item;
    }

    public T deleteWithId(UUID id, T item) {
        logger.debug("Removing item {} in {} repository", id, item.getClass());
        storedItems.remove(id, item);
        return item;
    }

    public void deleteAll(){
        logger.debug("Clear database");
        storedItems.clear();
    }

    protected Optional<T> getOptional(Predicate<T> predicate) {
        return storedItems.values().stream().filter(predicate).findAny();
    }
}
