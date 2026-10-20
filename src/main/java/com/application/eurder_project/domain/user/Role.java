package com.application.eurder_project.domain.user;

import com.application.eurder_project.security.Permission;

import java.util.List;

public enum Role {
    ADMIN(List.of(Permission.VIEW_ALL_USERS,
                  Permission.CREATE_USER,
                  Permission.CREATE_ITEM,
                  Permission.VIEW_ALL_ITEMS,
                  Permission.VIEW_ALL_ORDERS,
                  Permission.CREATE_ORDER)),
    CUSTOMER(List.of(Permission.VIEW_ALL_ITEMS,
                     Permission.CREATE_ORDER));

    final List<Permission> permissions;

    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
