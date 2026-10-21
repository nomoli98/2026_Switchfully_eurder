package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.user.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class EurderRepository extends FakeDatabase<Eurder> {

    public EurderRepository() {}

    public EurderRepository(List<Eurder> eurders) {
        eurders.forEach(this::save);
    }

    public Eurder save(Eurder eurder) {
        return saveWithId(eurder.getId(), eurder);
    }

    public List<Eurder> getByUser(User user) {
        return getAllOptionals(e -> e.getUser().equals(user));
    }

    public List<Eurder> getEurdersWithItemsShippedToday() {
        LocalDate today = LocalDate.now();
        return getAllOptionals(e -> e.getItems()
                                     .stream()
                                     .anyMatch(itemGroup -> itemGroup.getShippingDate()
                                                                     .equals(today)));
    }

    public List<Eurder> getMyEurdersWithItemsShippedToday(UUID userId) {
        LocalDate today = LocalDate.now();
        return getAllOptionals(e ->
                                       e.getUser().getId().equals(userId)
                                       && e.getItems().stream()
                                           .anyMatch(itemGroup -> today.equals(itemGroup.getShippingDate())));
    }
}
