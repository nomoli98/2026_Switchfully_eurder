package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Eurder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class EurderRepository extends FakeDatabase<Eurder> {

    public EurderRepository() {
        super(Eurder::getId);
    }

    public List<Eurder> getByUserId(UUID userId) {
        return filter(eurder -> eurder.getUser().getId().equals(userId));
    }
}
