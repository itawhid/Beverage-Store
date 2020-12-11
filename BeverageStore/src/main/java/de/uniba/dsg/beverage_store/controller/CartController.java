package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.dto.CheckoutDTO;
import de.uniba.dsg.beverage_store.dto.SubmitOrderDTO;
import de.uniba.dsg.beverage_store.model.Address;
import de.uniba.dsg.beverage_store.model.BeverageOrder;
import de.uniba.dsg.beverage_store.model.CartItem;
import de.uniba.dsg.beverage_store.model.User;
import de.uniba.dsg.beverage_store.repository.AddressRepository;
import de.uniba.dsg.beverage_store.repository.UserRepository;
import de.uniba.dsg.beverage_store.service.CartService;
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
import java.util.Optional;

@Controller
@RequestMapping(value = "/cart")
public class CartController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    public CartController(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
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

        return "cart";
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

        return "checkout";
    }

    @PostMapping(value = "/checkout")
    public String checkout(@Valid SubmitOrderDTO submitOrderDTO, Errors errors, Model model, Principal principal) {
        boolean hasModelError = false, hasServerError = false;

        if (errors.hasErrors()) {
            hasModelError = true;
        }

        if (!hasModelError) {
            Optional<User> optionalUser = userRepository.findUserByUsername(principal.getName());

            Optional<Address> optionalDeliveryAddress = addressRepository.findById(submitOrderDTO.getDeliveryAddressId());
            Optional<Address> optionalBillingAddress = addressRepository.findById(submitOrderDTO.getBillingAddressId());

            try {
                BeverageOrder beverageOrder = cartService.submitOrder(optionalUser.get(), optionalDeliveryAddress.get(), optionalBillingAddress.get());

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
        return addressRepository.findAllByUserUsername(username);
    }
}
