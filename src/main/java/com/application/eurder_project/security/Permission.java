package com.application.eurder_project.security;

public enum Permission {
    // users
    VIEW_ALL_USERS,
    VIEW_USER_BY_ID,
    CREATE_USER,
    // items
    CREATE_ITEM,
    UPDATE_ITEM,
    // eurders
    CREATE_ORDER,
    VIEW_ALL_ORDERS,
    VIEW_ORDERS_BY_USER,
    VIEW_MY_ORDERS,
    VIEW_ALL_SHIPPING_TODAY,
    // reports
    VIEW_MY_REPORT
}
