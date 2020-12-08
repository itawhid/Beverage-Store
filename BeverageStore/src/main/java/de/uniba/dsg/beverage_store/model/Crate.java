package de.uniba.dsg.beverage_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.uniba.dsg.beverage_store.validation.annotation.MoreThanZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Crate {
    @Id
    private Long id;

    @NotNull(message = "Name is required.")
    @NotEmpty(message = "Name can not be empty.")
    private String name;

    @NotNull(message = "Crate Picture is required")
    @Pattern(regexp = "(https:\\/\\/).*\\.(?:jpg|gif|png)", message = "Crate Pic Must be a valid URL to a picture.")
    private String cratePic;

    @Min(value = 0, message = "No of Bottles must be more then or equal to zero.")
    private int noOfBottles;

    @MoreThanZero(message = "Price must be more than zero.")
    private double price;

    @Min(value = 0, message = "In Stock must be more then or equal to zero.")
    private int inStock;

    //Entity Relations
    @ManyToOne(cascade = CascadeType.MERGE)
    private Bottle bottle;

    @OneToMany(mappedBy = "crate")
    @JsonBackReference
    private Set<BeverageOrderItem> beverageOrderItems;
}
