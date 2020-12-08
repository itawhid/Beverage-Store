package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.helper.Helper;
import de.uniba.dsg.beverage_store.model.*;
import de.uniba.dsg.beverage_store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartService {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    private CrateRepository crateRepository;

    @Autowired
    private BottleRepository bottleRepository;

    @Autowired
    BeverageOrderRepository beverageOrderRepository;

    @Autowired
    BeverageOrderItemRepository beverageOrderItemRepository;

    private int cartItemId;

    private final List<CartItem> cartItems;

    public CartService() {
        cartItemId = 0;
        cartItems = new ArrayList<>();
    }

    public CartItem addCartItem(BeverageType beverageType, Long beverageId) throws NotFoundException {
        CartItem cartItem = null;

        if (beverageType == BeverageType.BOTTLE) {
            Optional<Bottle> optionalBottle = bottleRepository.findById(beverageId);

            if (optionalBottle.isPresent()) {
                cartItem = buildBottleCartItem(optionalBottle.get());
            }
        } else if (beverageType == BeverageType.CRATE) {
            Optional<Crate> optionalCrate = crateRepository.findById(beverageId);

            if (optionalCrate.isPresent()) {
                cartItem = buildCrateCartItem(optionalCrate.get());
            }
        }

        if (cartItem == null) {
            throw new NotFoundException();
        }

        cartItem.setCartItemId(++cartItemId);
        cartItems.add(cartItem);

        return cartItem;
    }

    public void removeCartItem(Long cartItemId) throws NotFoundException {
        Optional<CartItem> optionalCartItem = cartItems.stream()
                .filter(x -> x.getCartItemId() == cartItemId)
                .findAny();

        if (!optionalCartItem.isPresent()) {
            throw new NotFoundException();
        }

        cartItems.remove(optionalCartItem.get());
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getCartItemCount() {
        return cartItems.size();
    }

    public double getCartTotal() {
        return cartItems.stream()
                .mapToDouble(x -> x.getPrice())
                .sum();
    }

    public BeverageOrder createOrder(User user, Address deliveryAddress, Address billingAddress) {
        BeverageOrder beverageOrder = new BeverageOrder(null, null, LocalDate.now(), getCartTotal(), user, deliveryAddress, billingAddress, null);
        beverageOrderRepository.save(beverageOrder);

        beverageOrder.setOrderNumber(Helper.generateOrderNumber(beverageOrder.getId()));
        beverageOrderRepository.save(beverageOrder);

        List<BeverageOrderItem> beverageOrderItems = new ArrayList<>();

        for (CartItem cartItem: cartItems) {
            beverageOrderItems.add(buildBeverageOrderItem(beverageOrder, cartItem.getBeverageType(), cartItem.getBeverageId()));
        }

        beverageOrderItemRepository.saveAll(beverageOrderItems);

        clearCart();

        return beverageOrder;
    }

    private void clearCart() {
        cartItems.clear();
    }

    private CartItem buildBottleCartItem(Bottle bottle) {
        CartItem cartItem = new CartItem();

        cartItem.setCartItemId(0);
        cartItem.setBeverageType(BeverageType.BOTTLE);
        cartItem.setBeverageId(bottle.getId());
        cartItem.setName(bottle.getName());
        cartItem.setPicUrl(bottle.getBottlePic());
        cartItem.setPrice(bottle.getPrice());
        cartItem.setInStock(bottle.getInStock());
        cartItem.setVolume(bottle.getVolume());
        cartItem.setVolumePercent(bottle.getVolumePercent());
        cartItem.setSupplier(bottle.getSupplier());

        return cartItem;
    }

    private CartItem buildCrateCartItem(Crate crate) {
        CartItem cartItem = new CartItem();

        cartItem.setCartItemId(0);
        cartItem.setBeverageType(BeverageType.CRATE);
        cartItem.setBeverageId(crate.getId());
        cartItem.setName(crate.getName());
        cartItem.setPicUrl(crate.getCratePic());
        cartItem.setPrice(crate.getPrice());
        cartItem.setInStock(crate.getInStock());
        cartItem.setNoOfBottle(crate.getNoOfBottles());

        return cartItem;
    }

    private BeverageOrderItem buildBeverageOrderItem(BeverageOrder beverageOrder, BeverageType beverageType, Long beverageId) {
        return new BeverageOrderItem(
                null,
                beverageType,
                beverageType == BeverageType.BOTTLE
                        ? bottleRepository.findById(beverageId).get()
                        : null,
                beverageType == BeverageType.BOTTLE
                        ? crateRepository.findById(beverageId).get()
                        : null,
                beverageOrder
        );
    }
}
