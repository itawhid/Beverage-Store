package de.uniba.dsg.beverage_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BEVERAGE_ORDER_ITEM")
@NamedEntityGraph(
        name = "OrderItem.orderItems",
        attributeNodes = {
                @NamedAttributeNode(value = "bottle"),
                @NamedAttributeNode(value = "crate")
        }
)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Beverage Type is required.")
    private BeverageType beverageType;

    @Min(value = 1, message = "Quantity must be at least one.")
    private int quantity;

    //Entity Relations
    @ManyToOne
    private Bottle bottle;

    @ManyToOne
    private Crate crate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private Order order;

    //Validation
    @PrePersist
    @PreUpdate
    public void validate() {
        if (beverageType == BeverageType.BOTTLE) {
            if (bottle == null) {
                throw new ValidationException("Bottle is required for Beverage Type Bottle");
            }

            if (crate != null) {
                throw new ValidationException("Crate should be null for Beverage Type Bottle");
            }
        } else if (beverageType == BeverageType.CRATE) {
            if (crate == null) {
                throw new ValidationException("Crate is required for Beverage Type Crate");
            }

            if (bottle != null) {
                throw new ValidationException("Bottle should be null for Beverage Type Crate");
            }
        }
    }
}