package com.application.eurder_project.repository;

import com.application.eurder_project.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends FakeDatabase<User> {

    public UserRepository() {
        super(User::getId);
    }

    public boolean existsByEmailIgnoreCase(String email) {
        return !filter(user -> user.getEmail().equalsIgnoreCase(email)).isEmpty();
    }
}
