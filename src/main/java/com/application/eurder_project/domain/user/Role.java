package com.application.eurder_project.domain.user;

import com.application.eurder_project.security.Permission;

import java.util.EnumSet;
import java.util.Set;

public enum Role {
    ADMIN(Permission.VIEW_ALL_USERS,
          Permission.VIEW_USER_BY_ID,
          Permission.CREATE_USER,
          Permission.CREATE_ITEM,
          Permission.UPDATE_ITEM,
          Permission.VIEW_ALL_ORDERS,
          Permission.VIEW_ORDERS_BY_USER,
          Permission.VIEW_ALL_SHIPPING_TODAY,
          Permission.CREATE_ORDER,
          Permission.VIEW_MY_REPORT),
    CUSTOMER(Permission.CREATE_ORDER,
             Permission.VIEW_MY_REPORT,
             Permission.VIEW_MY_ORDERS);

    private final Set<Permission> permissions;

    Role(Permission first, Permission... others) {
        this.permissions = EnumSet.of(first, others);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}
