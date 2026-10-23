package com.application.eurder_project.repository;

import com.application.eurder_project.logging.RepositoryLoggingService;
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
        List<T> res = storedItems.values().stream().toList();
        RepositoryLoggingService.getAllLog(res.size(), res.getClass().getName());
        return res;
    }

    public Optional<T> get(UUID id) {
        Optional<T> res = Optional.ofNullable(storedItems.get(id));
        RepositoryLoggingService.getOneLog(id, res.getClass().getName());
        return res;
    }

    protected T saveWithId(UUID id, T item) {
        RepositoryLoggingService.SaveLog(id, item.getClass().getName());
        storedItems.put(id, item);
        return item;
    }

    protected boolean update(UUID id, T item, T updatedItem) {
        RepositoryLoggingService.UpdateLog(id, item.getClass().getName());
        return storedItems.replace(id, item, updatedItem);
    }

    public T deleteWithId(UUID id, T item) {
        RepositoryLoggingService.DeleteLog(id, item.getClass().getName());
        storedItems.remove(id, item);
        return item;
    }

    public void reset() {
        logger.debug("Reset table");
        storedItems.clear();
    }

    protected List<T> getAllOptionals(Predicate<T> predicate) {
        return storedItems.values().stream().filter(predicate).toList();
    }


}
