package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.db.BeverageOrder;
import de.uniba.dsg.beverage_store.model.db.BeverageOrderItem;
import de.uniba.dsg.beverage_store.service.OrderService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Retrieving order page: " + page + " - start");

        Page<BeverageOrder> orderPage = orderService.getPagedOrdersByUsername(principal.getName(), page);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("numberOfPages", orderPage.getTotalPages());

        log.info("Retrieving order page: " + page + " - completed");

        return "order/list";
    }

    @GetMapping(value = "/{orderNumber}")
    public String getOrder(@PathVariable("orderNumber") String orderNumber, Model model) {
        log.info("Retrieving order with order number: " + orderNumber + " - start");

        try {
            BeverageOrder order = orderService.getOrderByOrderNumber(orderNumber);
            List<BeverageOrderItem> orderItems = orderService.getOrderItemsByOrderNumber(orderNumber);

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("orderNotFound", false);

            log.info("Retrieving order with order number: " + orderNumber + " - completed");
        } catch (NotFoundException ex) {
            model.addAttribute("orderNotFound", true);

            log.info("Retrieving order with order number: " + orderNumber + " - failed, found not found exception");
        }

        return "order/details";
    }
}
