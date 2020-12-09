package de.uniba.dsg.beverage_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.uniba.dsg.beverage_store.validation.annotation.MoreThanZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bottle {
    @Id
    private Long id;

    @NotNull(message = "Name is required.")
    @NotEmpty(message = "Name can not be empty.")
    private String name;

    @NotNull(message = "Bottle Picture is required")
    @Pattern(regexp = "(https:\\/\\/).*\\.(?:jpg|gif|png)", message = "Bottle Pic Must be a valid URL to a picture.")
    private String bottlePic;

    @MoreThanZero(message = "Volume must be more than zero.")
    private double volume;

    @Min(value = 0, message = "Volume Percent must be more then or equal to zero.")
    private double volumePercent;

    @MoreThanZero(message = "Price must be more than zero.")
    private double price;

    @NotNull(message = "Supplier is required.")
    @NotEmpty(message = "Supplier can not be empty.")
    private String supplier;

    @Min(value = 0, message = "In Stock must be more then or equal to zero.")
    private int inStock;

    //Entity Relations
    @OneToMany(mappedBy = "bottle")
    @JsonBackReference
    private Set<Crate> crates;

    @OneToMany(mappedBy = "bottle")
    @JsonBackReference
    private Set<BeverageOrderItem> beverageOrderItems;
}
