package de.uniba.dsg.beverage_store.model;

import de.uniba.dsg.beverage_store.validation.annotation.MoreThanZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class Beverage {
    @Id
    private Long id;

    @NotNull(message = "Name is required.")
    @NotEmpty(message = "Name can not be empty.")
    private String name;

    @NotNull(message = "Picture URL is required")
    @Pattern(regexp = "(https://).*\\.(?:jpg|gif|png)", message = "Picture URL Must be a valid URL to a picture.")
    private String picUrl;

    @MoreThanZero(message = "Price must be more than zero.")
    private double price;

    @Min(value = 0, message = "In Stock must be more then or equal to zero.")
    private int inStock;

    @Transient
    private int allowedInStock;

    public void setAllowedInStockToInStock() {
        allowedInStock = inStock;
    }

    public void decreaseAllowedInStock(int quantity) {
        this.allowedInStock -= quantity;
    }
}
