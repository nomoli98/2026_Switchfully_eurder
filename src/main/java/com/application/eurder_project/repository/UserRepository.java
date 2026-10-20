package com.application.eurder_project.repository;

import com.application.eurder_project.domain.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository extends FakeDatabase<User> {

    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository() {}

    public UserRepository(List<User> users) {
        users.forEach(this::save);
    }

    public User save(User user) {
        return saveWithId(user.getId(), user);
    }

}