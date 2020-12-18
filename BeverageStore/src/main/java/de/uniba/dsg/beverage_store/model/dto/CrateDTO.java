package de.uniba.dsg.beverage_store.model.dto;

import de.uniba.dsg.beverage_store.validation.annotation.MoreThanZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CrateDTO extends BeverageDTO {
    @MoreThanZero(message = "No of Bottles must be more then zero.")
    private int noOfBottles;

    @MoreThanZero(message = "Bottle is required.")
    private long bottleId;
}
