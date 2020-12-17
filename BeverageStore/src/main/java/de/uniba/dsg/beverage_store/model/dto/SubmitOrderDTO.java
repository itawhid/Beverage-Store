package de.uniba.dsg.beverage_store.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitOrderDTO {
    @Min(value = 1, message = "Delivery Address is required.")
    private long deliveryAddressId;

    @Min(value = 1, message = "Billing Address is required.")
    private long billingAddressId;
}
