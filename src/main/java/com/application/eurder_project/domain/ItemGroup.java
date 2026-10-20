package com.application.eurder_project.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

public class ItemGroup {
    private Item item;
    private int amount;
    private double groupPrice;
    private LocalDate shippingDate;

    public ItemGroup(Item item, int amount) {
        this.item = item;
        this.amount = amount;
        this.groupPrice = calculateGroupPrice();
        this.shippingDate = calculateShippingDate();
    }


    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public double getGroupPrice() {
        return groupPrice;
    }

    public LocalDate getShippingDate() {
        return shippingDate;
    }

    public double calculateGroupPrice(){
        return amount * item.getPrice();
    }

    private LocalDate calculateShippingDate() {
        if(getItem().getStock() < getAmount()) return LocalDate.now().plusDays(7);
        return LocalDate.now().plusDays(1);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemGroup itemGroup = (ItemGroup) o;
        return amount == itemGroup.amount && Objects.equals(item, itemGroup.item) &&
               Objects.equals(shippingDate, itemGroup.shippingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, amount, shippingDate);
    }
}
