package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EurderRepository extends FakeDatabase<Eurder> {

    public EurderRepository() {}

    public EurderRepository(List<Eurder> eurders) {
        eurders.forEach(this::save);
    }

    public Eurder save(Eurder eurder) {
        return saveWithId(eurder.getId(), eurder);
    }

    public List<Eurder> getByUser(User user){
        return getAllOptionals(i -> i.getUser().equals(user));
    }
}
