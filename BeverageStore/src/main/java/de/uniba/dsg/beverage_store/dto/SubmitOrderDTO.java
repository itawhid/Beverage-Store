package de.uniba.dsg.beverage_store.dto;

import javax.validation.constraints.Min;

public class SubmitOrderDTO {
    @Min(value = 1, message = "Delivery Address is required.")
    private int deliveryAddressId;

    @Min(value = 1, message = "Billing Address is required.")
    private int billingAddressId;
}
