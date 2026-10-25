package com.application.eurder_project.domain.user;

import com.application.eurder_project.security.Permission;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final String phoneNumber;
    private final Address address;
    private final Role role;

    public User(String firstname,
                String lastname,
                String email,
                String phoneNumber,
                Address address,
                Role role) {
        this.id = UUID.randomUUID();
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public Role getRole() {
        return role;
    }

    public boolean hasPermission(Permission permission) {
        return role.hasPermission(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((User) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
