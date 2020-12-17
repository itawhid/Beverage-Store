package de.uniba.dsg.beverage_store.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@NamedEntityGraph(
        name = "Crate.crates",
        attributeNodes = {
                @NamedAttributeNode(value = "bottle")
        }
)
public class Crate extends Beverage {
    public Crate(Long id, String name, String picUrl, int noOfBottles, double price, int inStock, Bottle bottle, Set<OrderItem> orderItems) {
        super(id, name, picUrl, price, inStock, inStock);

        this.noOfBottles = noOfBottles;
        this.bottle = bottle;
        this.orderItems = orderItems;
    }

    @Min(value = 0, message = "No of Bottles must be more then or equal to zero.")
    private int noOfBottles;

    //Entity Relations
    @ManyToOne(cascade = CascadeType.MERGE)
    private Bottle bottle;

    @OneToMany(mappedBy = "crate")
    @JsonBackReference
    private Set<OrderItem> orderItems;
}
