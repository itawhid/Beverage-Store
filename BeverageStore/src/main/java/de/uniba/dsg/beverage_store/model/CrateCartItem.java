package de.uniba.dsg.beverage_store.model;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class CrateCartItem extends BeverageCartItem {
    private int noOfBottle;
}
