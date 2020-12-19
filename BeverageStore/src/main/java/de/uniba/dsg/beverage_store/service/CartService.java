package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.InsufficientStockException;
import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.*;
import de.uniba.dsg.beverage_store.model.db.Bottle;
import de.uniba.dsg.beverage_store.model.db.Crate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartService {

    @Autowired
    private BeverageService beverageService;

    private int cartItemId;

    private final List<CartItem> cartItems;

    public CartService() {
        cartItemId = 0;
        cartItems = new ArrayList<>();
    }

    public CartItem addCartItem(BeverageType beverageType, Long beverageId, int quantity) throws NotFoundException, InsufficientStockException {
        CartItem cartItem;

        Optional<CartItem> cartItemOptional = getCartBeverage(beverageId, beverageType);

        if (cartItemOptional.isPresent()) {
            cartItem = cartItemOptional.get();

            if ((cartItem.getQuantity() + quantity) > cartItem.getInStock()) {
                throw new InsufficientStockException("Insufficient stock for " + beverageType.name() + " with ID: " + beverageId);
            }

            cartItem.addQuantity(quantity);

            return cartItem;
        }

        cartItem = retrieveCartItem(beverageType, beverageId, quantity);
        if (cartItem == null) {
            throw new NotFoundException();
        }

        if (cartItem.getQuantity() > cartItem.getInStock()) {
            throw new InsufficientStockException("Insufficient stock for " + beverageType.name() + " with ID: " + beverageId);
        }

        cartItem.setCartItemId(++cartItemId);
        cartItems.add(cartItem);

        return cartItem;
    }

    public void removeCartItem(Long cartItemId) throws NotFoundException {
        Optional<CartItem> optionalCartItem = cartItems.stream()
                .filter(x -> x.getCartItemId() == cartItemId)
                .findAny();

        if (optionalCartItem.isEmpty()) {
            throw new NotFoundException();
        }

        cartItems.remove(optionalCartItem.get());
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getCartItemCount() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double getCartTotal() {
        return cartItems.stream()
                .mapToDouble(x -> (x.getQuantity() * x.getPrice()))
                .sum();
    }

    public void clearCart() {
        cartItems.clear();
    }

    private CartItem buildBottleCartItem(Bottle bottle, int quantity) {
        CartItem cartItem = new CartItem();

        cartItem.setCartItemId(0);
        cartItem.setBeverageType(BeverageType.BOTTLE);
        cartItem.setBeverageId(bottle.getId());
        cartItem.setQuantity(quantity);
        cartItem.setName(bottle.getName());
        cartItem.setPicUrl(bottle.getPicUrl());
        cartItem.setPrice(bottle.getPrice());
        cartItem.setInStock(bottle.getInStock());
        cartItem.setVolume(bottle.getVolume());
        cartItem.setVolumePercent(bottle.getVolumePercent());
        cartItem.setSupplier(bottle.getSupplier());

        return cartItem;
    }

    private CartItem buildCrateCartItem(Crate crate, int quantity) {
        CartItem cartItem = new CartItem();

        cartItem.setCartItemId(0);
        cartItem.setBeverageType(BeverageType.CRATE);
        cartItem.setBeverageId(crate.getId());
        cartItem.setQuantity(quantity);
        cartItem.setName(crate.getName());
        cartItem.setPicUrl(crate.getPicUrl());
        cartItem.setPrice(crate.getPrice());
        cartItem.setInStock(crate.getInStock());
        cartItem.setNoOfBottle(crate.getNoOfBottles());

        return cartItem;
    }

    private Optional<CartItem> getCartBeverage(long beverageId, BeverageType beverageType) {
        return cartItems.stream()
                .filter(x -> x.getBeverageId() == beverageId && x.getBeverageType() == beverageType)
                .findAny();
    }

    private CartItem retrieveCartItem(BeverageType beverageType, Long beverageId, int quantity) throws NotFoundException {
        if (beverageType == BeverageType.BOTTLE) {
            Bottle bottle = beverageService.getBottleById(beverageId);

            return buildBottleCartItem(bottle, quantity);
        } else if (beverageType == BeverageType.CRATE) {
            Crate crate = beverageService.getCrateById(beverageId);

            return buildCrateCartItem(crate, quantity);
        }

        return null;
    }
}
