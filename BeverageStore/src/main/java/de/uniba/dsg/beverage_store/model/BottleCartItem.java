package de.uniba.dsg.beverage_store.model;

import lombok.Data;

@Data
public class BottleCartItem extends BeverageCartItem {
    private double volume;
    private double volumePercent;
    private String supplier;
}
