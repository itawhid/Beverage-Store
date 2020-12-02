package de.uniba.dsg.beverage_store.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class CheckoutDTO {
    @Min(value = 1, message = "At least one Cart Item is required for checkout.")
    private int cartItemCount;
}
