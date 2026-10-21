package com.application.eurder_project.domain.user;

import com.application.eurder_project.security.Permission;

import java.util.List;

public enum Role {
    ADMIN(List.of(Permission.VIEW_ALL_USERS,
                  Permission.VIEW_USER_BY_ID,
                  Permission.CREATE_USER,
                  Permission.CREATE_ITEM,
                  Permission.UPDATE_ITEM,
                  Permission.VIEW_ALL_ORDERS,
                  Permission.VIEW_ORDERS_BY_USER,
                  Permission.CREATE_ORDER)),
    CUSTOMER(List.of(Permission.CREATE_ORDER,
                     Permission.VIEW_MY_REPORT));

    final List<Permission> permissions;

    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
