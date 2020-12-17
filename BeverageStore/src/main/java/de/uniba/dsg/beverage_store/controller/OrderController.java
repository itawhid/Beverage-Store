package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.Order;
import de.uniba.dsg.beverage_store.model.OrderItem;
import de.uniba.dsg.beverage_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getOrders(@RequestParam(defaultValue = "1") int page, Model model, Principal principal) {
        Page<Order> orderPage = orderService.getPagedOrdersByUsername(principal.getName(), page);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("numberOfPages", orderPage.getTotalPages());

        return "order/list";
    }

    @GetMapping(value = "/{orderNumber}")
    public String getOrder(@PathVariable("orderNumber") String orderNumber, Model model) {
        try {
            Order order = orderService.getOrderByOrderNumber(orderNumber);
            List<OrderItem> orderItems = orderService.getOrderItemsByOrderNumber(orderNumber);

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("orderNotFound", false);
        } catch (NotFoundException ex) {
            model.addAttribute("orderNotFound", true);
        }

        return "order/details";
    }
}
