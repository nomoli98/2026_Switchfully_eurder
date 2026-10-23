package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

@Repository
public class ItemRepository extends FakeDatabase<Item> {

    public ItemRepository() {}

    public ItemRepository(List<Item> items) {items.forEach(this::save);}

    public Item save(Item item) {
        return saveWithId(item.getId(), item);
    }

    public Item updateItem(UUID id, Item item, Item updatedItem) {
        if (!update(id, item, updatedItem)) {
            throw new ConcurrentModificationException(
                    "Item " + id + " was modified or removed by another request");
        }
        return updatedItem;
    }

}
