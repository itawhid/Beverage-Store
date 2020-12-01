package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.service.CartService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/cart-items")
public class CartItemController {

    @Resource(name = "sessionScopedCartService")
    CartService cartService;
}
