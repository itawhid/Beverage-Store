package de.uniba.dsg.beverage_store.model;

import lombok.Data;

@Data
public class BeverageCartItem {
    private int cartItemId;
    private BeverageType beverageType;
    private long beverageId;
    private String name;
    private String picUrl;
    private double price;
    private int inStock;
}
