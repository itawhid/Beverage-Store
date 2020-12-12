package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.BeverageOrder;
import de.uniba.dsg.beverage_store.model.BeverageOrderItem;
import de.uniba.dsg.beverage_store.service.BeverageOrderService;
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

    private final BeverageOrderService beverageOrderService;

    @Autowired
    public OrderController(BeverageOrderService beverageOrderService) {
        this.beverageOrderService = beverageOrderService;
    }

    @GetMapping
    public String getOrders(@RequestParam(defaultValue = "1") int page, Model model, Principal principal) {
        Page<BeverageOrder> beverageOrderPage = beverageOrderService.getPagedBeverageOrdersByUsername(principal.getName(), page);

        model.addAttribute("orders", beverageOrderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("numberOfPages", beverageOrderPage.getTotalPages());

        return "orders/list";
    }

    @GetMapping(value = "/{orderNumber}")
    public String getOrder(@PathVariable("orderNumber") String orderNumber, Model model) {
        try {
            BeverageOrder order = beverageOrderService.getBeverageOrderByOrderNumber(orderNumber);
            List<BeverageOrderItem> orderItems = beverageOrderService.getBeverageOrderItemsByOrderNumber(orderNumber);

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("orderNotFound", false);
        } catch (NotFoundException ex) {
            model.addAttribute("orderNotFound", true);
        }

        return "orders/details";
    }
}
