package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.dto.CheckoutDTO;
import de.uniba.dsg.beverage_store.model.CartItem;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/cart")
public class CartController {

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

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

    @GetMapping(value = "/checkout")
    public String getCheckout(Model model) {
        return "checkout";
    }

    @PostMapping(value = "/checkout")
    public String checkout(@Valid CheckoutDTO checkoutDTO, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute("hasCheckoutError", true);

            return "redirect:/cart";
        }

        return "redirect:/cart/checkout";
    }
}
