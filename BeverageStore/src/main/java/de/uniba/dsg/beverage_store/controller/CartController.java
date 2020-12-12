package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.dto.CheckoutDTO;
import de.uniba.dsg.beverage_store.dto.SubmitOrderDTO;
import de.uniba.dsg.beverage_store.model.Address;
import de.uniba.dsg.beverage_store.model.BeverageOrder;
import de.uniba.dsg.beverage_store.model.CartItem;
import de.uniba.dsg.beverage_store.model.User;
import de.uniba.dsg.beverage_store.service.AddressService;
import de.uniba.dsg.beverage_store.service.BeverageOrderService;
import de.uniba.dsg.beverage_store.service.CartService;
import de.uniba.dsg.beverage_store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/cart")
public class CartController {

    private final UserService userService;
    private final AddressService addressService;
    private final BeverageOrderService beverageOrderService;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    public CartController(UserService userService, AddressService addressService, BeverageOrderService beverageOrderService) {
        this.userService = userService;
        this.addressService = addressService;
        this.beverageOrderService = beverageOrderService;
    }

    @GetMapping
    public String getCart(Model model, HttpServletRequest request) {
        List<CartItem> cartItems = cartService.getCartItems();
        double cartTotal = cartService.getCartTotal();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null && inputFlashMap.get("hasCheckoutError") != null && (boolean)inputFlashMap.get("hasCheckoutError")) {
            model.addAttribute("hasCheckoutError", true);
        } else {
            model.addAttribute("hasCheckoutError", false);
        }

        return "cart/details";
    }

    @PostMapping(value = "/checkout/process")
    public String processCheckout(@Valid CheckoutDTO checkoutDTO, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("hasCheckoutError", true);

            return "redirect:/cart";
        }

        return "redirect:/cart/checkout";
    }

    @GetMapping(value = "/checkout")
    public String getCheckout(Model model, Principal principal) {
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
                User user = userService.getUserByUserName(principal.getName());
                Address deliveryAddress = addressService.getAddressById(submitOrderDTO.getDeliveryAddressId());
                Address billingAddress = addressService.getAddressById(submitOrderDTO.getBillingAddressId());

                BeverageOrder beverageOrder = beverageOrderService.createOrder(user, deliveryAddress, billingAddress);

                return "redirect:/order/" + beverageOrder.getOrderNumber();
            } catch (Exception ex) {
                hasServerError = true;
            }
        }

        model.addAttribute("addresses", getAddressesByUsername(principal.getName()));
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        model.addAttribute("cartTotal", cartService.getCartTotal());

        model.addAttribute("hasServerError", hasServerError);

        return "checkout";
    }

    private List<Address> getAddressesByUsername(String username) {
        return addressService.getAllByUsername(username);
    }
}
