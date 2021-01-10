package de.uniba.dsg.beverage_store.model.dto;

import de.uniba.dsg.beverage_store.model.BeverageType;
import de.uniba.dsg.beverage_store.validation.annotation.MoreThanZero;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CartItemDTO {
    @MoreThanZero(message = "Beverage Id must be greater than zero.")
    private Long beverageId;

    @NotNull(message = "Beverage Type is required.")
    private BeverageType beverageType;

    @MoreThanZero( message = "Quantity must be greater than zero.")
    private int quantity;
}
