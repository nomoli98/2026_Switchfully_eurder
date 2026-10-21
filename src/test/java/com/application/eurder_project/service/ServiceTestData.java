package com.application.eurder_project.service;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.Address;
import com.application.eurder_project.domain.user.Role;
import com.application.eurder_project.domain.user.User;

import java.util.ArrayList;
import java.util.List;

class ServiceTestData {

    public static User validAdmin() {
        return new User("Tom",
                        "Mayo",
                        "tom@sfth.uk",
                        "0123456788",
                        new Address("Shropshire Road",
                                    8,
                                    "Shropshire",
                                    "2034",
                                    "United Kingdom"),
                        Role.ADMIN);
    }

    public static List<User> validUserList() {
        return List.of(new User("Alexander",
                                "Jeremy",
                                "aj@sfth.uk",
                                "0123456789",
                                new Address("Shropshire Road",
                                            8,
                                            "Shropshire",
                                            "2034",
                                            "United Kingdom"),
                                Role.CUSTOMER),
                       new User("Luke",
                                "Manning",
                                "luke@sfth.uk",
                                "0123456784",
                                new Address("Shropshire Road",
                                            8,
                                            "Shropshire",
                                            "2034",
                                            "United Kingdom"),
                                Role.CUSTOMER),
                       new User("Sam",
                                "Russel",
                                "sam@sfth.uk",
                                "0123456786",
                                new Address("Shropshire Road",
                                            8,
                                            "Shropshire",
                                            "2034",
                                            "United Kingdom"),
                                Role.CUSTOMER)
                      );
    }

    public static Item validItem() {
        return new Item("Cup", "Object to pour liquids", 10.99, 5);
    }

    public static List<Item> validItemList() {
        return List.of(new Item("Cup", "Object to pour liquids", 10.99, 5),
                       new Item("Fork", "Object to stab solids", 5.99, 10),
                       new Item("Knife", "Object to cut solids", 5.99, 5),
                       new Item("Spoon", "Object to scoop liquids", 3.99, 10));
    }

    public static ItemGroup validItemGroup() {
        return new ItemGroup(validItem(), 5);
    }

    public static List<ItemGroup> validItemGroupList() {
        List<ItemGroup> groups = new ArrayList<>();
        List<Item> items = validItemList();
        items.forEach(i -> groups.add(new ItemGroup(i, 5)));
        return groups;
    }

    public static Eurder validEurder(User user) {
        return new Eurder(validItemGroupList(), user);
    }

    public static List<Eurder> validEurderList(User user) {
        List<Eurder> eurders = new ArrayList<>();
        List<Item> items = validItemList();
        items.forEach(i -> eurders.add(new Eurder(validItemGroupList(), user)));
        return eurders;
    }

}