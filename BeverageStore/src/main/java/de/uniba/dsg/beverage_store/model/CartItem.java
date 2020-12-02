package de.uniba.dsg.beverage_store.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private int cartItemId;
    private BeverageType beverageType;
    private long beverageId;
    private String name;
    private String picUrl;
    private double price;
    private int inStock;
    private double volume;
    private double volumePercent;
    private String supplier;
    private int noOfBottle;
}
