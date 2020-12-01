package de.uniba.dsg.beverage_store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/orders")
public class OrdersController {

    @GetMapping
    public String getOrders() {
        return "orders";
    }
}
