package com.application.eurder_project.domain;

import com.application.eurder_project.controller.security.Permission;

import java.util.List;

public enum Role {
    ADMIN (List.of(Permission.CREATE_USER,
           Permission.CREATE_USER)),
    CUSTOMER(List.of());

    final List<Permission> permissions;

    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
