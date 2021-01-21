package de.uniba.dsg.cloudfunction.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    @NotNull(message = "Order Number is required.")
    @NotEmpty(message = "Order Number cannot be empty.")
    private String orderNumber;

    @NotNull(message = "Order Date is required.")
    private LocalDate orderDate;

    @NotNull(message = "Customer Name is required.")
    @NotEmpty(message = "Customer Name cannot be empty.")
    private String customerName;

    @Email(message="Please provide a valid Customer Email ID.")
    private String customerEmailId;

    @NotNull(message = "Delivery Address is required.")
    private Address deliveryAddress;

    @NotNull(message = "Billing Address is required.")
    private Address billingAddress;

    @NotEmpty(message = "At least one Order Item is required.")
    private List<OrderItem> orderItems;

    public Order(String orderNumber, LocalDate orderDate, String customerName, String customerEmailId, Address deliveryAddress, Address billingAddress, List<OrderItem> orderItems) {
        this.setOrderNumber(orderNumber);
        this.setOrderDate(orderDate);
        this.setCustomerName(customerName);
        this.setCustomerEmailId(customerEmailId);
        this.setDeliveryAddress(deliveryAddress);
        this.setBillingAddress(billingAddress);
        this.setOrderItems(orderItems);
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmailId() {
        return customerEmailId;
    }

    public void setCustomerEmailId(String customerEmailId) {
        this.customerEmailId = customerEmailId;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public double getTotalPrice() {
        return getOrderItems().stream()
                .map(x -> x.getPrice() * x.getQuantity())
                .collect(Collectors.summingDouble(Double::doubleValue));
    }
}
