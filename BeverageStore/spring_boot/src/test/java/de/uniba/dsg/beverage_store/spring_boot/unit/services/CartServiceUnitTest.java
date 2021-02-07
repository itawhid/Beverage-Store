package de.uniba.dsg.beverage_store.spring_boot.unit.services;

import de.uniba.dsg.beverage_store.spring_boot.demo.DemoData;
import de.uniba.dsg.beverage_store.spring_boot.exception.InsufficientStockException;
import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.model.BeverageType;
import de.uniba.dsg.beverage_store.spring_boot.model.CartItem;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Bottle;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Crate;
import de.uniba.dsg.beverage_store.spring_boot.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CartServiceUnitTest {

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @BeforeEach
    public void init() throws NotFoundException, InsufficientStockException {
        cartService.clearCart();

        cartService.addCartItem(BeverageType.CRATE, getCrate().getId(), 2);
        cartService.addCartItem(BeverageType.BOTTLE, getBottle().getId(), 2);
    }

    @Test
    public void addCartItem_test() throws NotFoundException, InsufficientStockException {
        cartService.addCartItem(BeverageType.CRATE, getCrate().getId(), 2);
        cartService.addCartItem(BeverageType.BOTTLE, getBottle().getId(), 2);

        assertEquals(8, cartService.getCartItemCount());
        assertEquals(2, cartService.getCartItems().size());
    }

    @Test
    public void removeCartItem_test() throws NotFoundException {
        CartItem cartItem = cartService.getCartItems()
                .stream()
                .findFirst()
                .orElse(null);

        assertNotNull(cartItem);

        int cartItemQuantity = cartItem.getQuantity();
        double cartItemPrice = cartItem.getItemTotal();

        double totalPriceBeforeRemove = cartService.getCartTotal();
        int totalCartItemCountBeforeRemove = cartService.getCartItemCount();

        cartService.removeCartItem(cartItem.getCartItemId());

        assertEquals(1, cartService.getCartItems().size());
        assertEquals(totalPriceBeforeRemove - cartItemPrice, cartService.getCartTotal());
        assertEquals(totalCartItemCountBeforeRemove - cartItemQuantity, cartService.getCartItemCount());

        assertThrows(NotFoundException.class, () -> cartService.removeCartItem(0));
    }

    @Test
    public void getCartItems_test() {
        assertEquals(2, cartService.getCartItems().size());
    }

    @Test
    public void getCartItemCount_test() {
        assertEquals(4, cartService.getCartItemCount());
    }

    @Test
    public void getCartTotal_test() {
        List<CartItem> cartItems = cartService.getCartItems();

        assertEquals(cartItems.stream().mapToDouble(CartItem::getItemTotal).sum(), cartService.getCartTotal());
    }

    @Test
    public void clearCart_test() {
        cartService.clearCart();

        assertEquals(0.0, cartService.getCartTotal());
        assertEquals(0, cartService.getCartItemCount());
        assertEquals(0, cartService.getCartItems().size());
    }

    private Crate getCrate() {
        return DemoData.crates.stream()
                .findFirst()
                .orElse(null);
    }

    private Bottle getBottle() {
        return DemoData.bottles.stream()
                .findFirst()
                .orElse(null);
    }
}
