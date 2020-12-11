package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.BeverageOrder;
import de.uniba.dsg.beverage_store.model.BeverageOrderItem;
import de.uniba.dsg.beverage_store.repository.BeverageOrderItemRepository;
import de.uniba.dsg.beverage_store.repository.BeverageOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

    private final BeverageOrderRepository beverageOrderRepository;
    private final BeverageOrderItemRepository beverageOrderItemRepository;

    @Autowired
    public OrderController(BeverageOrderRepository beverageOrderRepository, BeverageOrderItemRepository beverageOrderItemRepository) {
        this.beverageOrderRepository = beverageOrderRepository;
        this.beverageOrderItemRepository = beverageOrderItemRepository;
    }

    @GetMapping
    public String getOrders(Model model, Principal principal) {
        List<BeverageOrder> orders = beverageOrderRepository.findAllByUserUsernameOrderByOrderNumber(principal.getName());

        model.addAttribute("orders", orders);

        return "orders";
    }

    @GetMapping(value = "/{orderNumber}")
    public String getOrder(@PathVariable("orderNumber") String orderNumber, Model model) {
        Optional<BeverageOrder> orderOptional = beverageOrderRepository.findBeverageOrderByOrderNumber(orderNumber);

        if (orderOptional.isPresent()) {
            BeverageOrder order = orderOptional.get();

            List<BeverageOrderItem> orderItems = beverageOrderItemRepository.findAllByBeverageOrderOrderNumber(order.getOrderNumber());

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            model.addAttribute("orderNotFound", false);
        } else {
            model.addAttribute("orderNotFound", true);
        }

        return "order-details";
    }
}
