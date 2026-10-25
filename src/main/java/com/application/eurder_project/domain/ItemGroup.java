package com.application.eurder_project.domain;

import java.time.LocalDate;
import java.util.Objects;

public class ItemGroup {
    private static final int DAYS_TO_SHIP_WHEN_IN_STOCK = 1;
    private static final int DAYS_TO_SHIP_WHEN_NOT_IN_STOCK = 7;

    private final Item item;
    private final String itemName;
    private final double unitPrice;
    private final int amount;
    private final LocalDate shippingDate;

    public ItemGroup(Item item, int amount) {
        this(item, amount, calculateShippingDate(item, amount));
    }

    public ItemGroup(Item item, int amount, LocalDate shippingDate) {
        this.item = item;
        this.itemName = item.getName();
        this.unitPrice = item.getPrice();
        this.amount = amount;
        this.shippingDate = shippingDate;
    }

    private static LocalDate calculateShippingDate(Item item, int amount) {
        int daysToShip = item.hasStockFor(amount) ? DAYS_TO_SHIP_WHEN_IN_STOCK : DAYS_TO_SHIP_WHEN_NOT_IN_STOCK;
        return LocalDate.now().plusDays(daysToShip);
    }

    public Item getItem() {
        return item;
    }

    public String getItemName() {
        return itemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getAmount() {
        return amount;
    }

    public double getGroupPrice() {
        return amount * unitPrice;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemGroup other = (ItemGroup) o;
        return amount == other.amount && Objects.equals(item, other.item) &&
               Objects.equals(shippingDate, other.shippingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, amount, shippingDate);
    }
}
