package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.dto.SubmitOrderDTO;
import de.uniba.dsg.beverage_store.model.db.Address;
import de.uniba.dsg.beverage_store.model.db.Order;
import de.uniba.dsg.beverage_store.model.CartItem;
import de.uniba.dsg.beverage_store.service.AddressService;
import de.uniba.dsg.beverage_store.service.OrderService;
import de.uniba.dsg.beverage_store.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/cart")
public class CartController {

    private final AddressService addressService;
    private final OrderService orderService;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    public CartController(AddressService addressService, OrderService orderService) {
        this.addressService = addressService;
        this.orderService = orderService;
    }

    @GetMapping
    public String getCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        double cartTotal = cartService.getCartTotal();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartItemCount", cartService.getCartItemCount());

        return "cart/details";
    }

    @GetMapping(value = "/checkout")
    public String getCheckout(Model model, Principal principal) {
        model.addAttribute("isEmptyCart", (cartService.getCartItemCount() == 0));
        model.addAttribute("addresses", getAddressesByUsername(principal.getName()));
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        model.addAttribute("cartTotal", cartService.getCartTotal());

        model.addAttribute("submitOrderDTO", new SubmitOrderDTO());

        return "cart/checkout";
    }

    @PostMapping(value = "/checkout")
    public String checkout(@Valid SubmitOrderDTO submitOrderDTO, Errors errors, Model model, Principal principal) {
        boolean hasModelError = false, hasServerError = false;

        if (errors.hasErrors()) {
            hasModelError = true;
        }

        if (!hasModelError) {
            try {
                Order order = orderService.createOrder(principal.getName(), submitOrderDTO.getDeliveryAddressId(), submitOrderDTO.getBillingAddressId());

                return "redirect:/order/" + order.getOrderNumber();
            } catch (Exception ex) {
                hasServerError = true;
            }
        }

        model.addAttribute("isEmptyCart", (cartService.getCartItemCount() == 0));
        model.addAttribute("addresses", getAddressesByUsername(principal.getName()));
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        model.addAttribute("cartTotal", cartService.getCartTotal());

        model.addAttribute("hasServerError", hasServerError);

        return "cart/checkout";
    }

    private List<Address> getAddressesByUsername(String username) {
        return addressService.getAllByUsername(username);
    }
}
