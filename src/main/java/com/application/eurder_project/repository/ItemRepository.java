package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Item;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository extends FakeDatabase<Item> {

    public ItemRepository() {
        super(Item::getId);
    }
}
