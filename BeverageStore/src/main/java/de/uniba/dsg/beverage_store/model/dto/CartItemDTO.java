package de.uniba.dsg.beverage_store.model.dto;

import de.uniba.dsg.beverage_store.model.BeverageType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartItemDTO {
    @Min(value = 1, message = "Beverage Id must be greater than zero.")
    private Long beverageId;

    @NotNull(message = "Beverage Type is required.")
    private BeverageType beverageType;

    @Min(value = 1, message = "Quantity must be at least one.")
    private int quantity;
}
