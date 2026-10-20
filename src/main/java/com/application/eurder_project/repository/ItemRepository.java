package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemRepository extends FakeDatabase<Item> {

    private final Logger logger = LoggerFactory.getLogger(ItemRepository.class);

    public ItemRepository() {}

    public ItemRepository(List<Item> items) {items.forEach(this::save);}

    public Item save(Item item) {
        return saveWithId(item.getId(), item);
    }
}
