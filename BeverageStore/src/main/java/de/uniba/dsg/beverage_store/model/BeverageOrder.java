package de.uniba.dsg.beverage_store.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.uniba.dsg.beverage_store.validation.annotation.MoreThanZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(
        name = "BeverageOrder.beverageOrders",
        attributeNodes = {
                @NamedAttributeNode(value = "user"),
                @NamedAttributeNode(value = "deliveryAddress"),
                @NamedAttributeNode(value = "billingAddress")
        }
)
public class BeverageOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @MoreThanZero(message = "Price must be more than zero.")
    private double price;

    //Entity Relations
    @ManyToOne
    @JsonIgnoreProperties({"addresses", "beverageOrders"})
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({"user", "deliveryAddressBeverageOrders", "billingAddressBeverageOrders"})
    private Address deliveryAddress;

    @ManyToOne
    @JsonIgnoreProperties({"user", "deliveryAddressBeverageOrders", "billingAddressBeverageOrders"})
    private Address billingAddress;

    @OneToMany(mappedBy = "beverageOrder", orphanRemoval = true)
    @JsonBackReference
    private Set<BeverageOrderItem> beverageOrderItems;
}
