package de.uniba.dsg.cloudfunction.models;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Address {
    @NotNull(message = "Street is required.")
    @NotEmpty(message = "Street cannot be empty.")
    private String street;

    @NotNull(message = "House Number is required.")
    @NotEmpty(message = "House Number cannot be empty.")
    private String houseNumber;

    @NotNull(message = "Postal Code is required.")
    @Pattern(regexp = "\\b\\d{5}\\b", message = "Valid Postal Code is required.")
    private String postalCode;

    public Address(String street, String houseNumber, String postalCode) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street.replace("ß", "ss");
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
