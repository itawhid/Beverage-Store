package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.model.BeverageCartItem;
import de.uniba.dsg.beverage_store.model.BeverageType;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private List<BeverageCartItem> cartItems;

    public CartService() {
        cartItems = new ArrayList<>();
    }

    public BeverageCartItem addCartItem(BeverageType beverageType, Long beverageId) {
        return null;
    }

    public void removeCartItem(Integer cartItemId) {

    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<BeverageCartItem> getCartItems() {
        return cartItems;
    }
}
