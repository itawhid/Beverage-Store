package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/orders")
public class OrdersController {

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @GetMapping
    public String getOrders(Model model) {
        model.addAttribute("cartItemCount", cartService.getCartItemCount());

        return "orders";
    }
}
