package com.application.eurder_project.domain;

import com.application.eurder_project.security.Permission;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class User {
    private final UUID id;
    private String firstname;
    private String lastname;
    private final String email;
    private String phoneNumber;
    private Address address;
    private Role role;

    public User(String firstname, String lastname, String email, String phoneNumber, Address address, Role role) {
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
        List<Permission> permissions = getRole().permissions;
        return permissions.contains(permission);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User customer = (User) o;
        return Objects.equals(id, customer.id) && Objects.equals(firstname, customer.firstname) && Objects.equals(lastname, customer.lastname) && Objects.equals(email, customer.email) && Objects.equals(phoneNumber, customer.phoneNumber) && Objects.equals(address, customer.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, phoneNumber, address);
    }
}
