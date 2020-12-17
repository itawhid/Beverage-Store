package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.helper.Helper;
import de.uniba.dsg.beverage_store.model.*;
import de.uniba.dsg.beverage_store.model.db.*;
import de.uniba.dsg.beverage_store.properties.OrderProperties;
import de.uniba.dsg.beverage_store.repository.OrderItemRepository;
import de.uniba.dsg.beverage_store.repository.OrderRepository;
import de.uniba.dsg.beverage_store.repository.BottleRepository;
import de.uniba.dsg.beverage_store.repository.CrateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrderService {

    private final UserService userService;
    private final AddressService addressService;
    private final BeverageService beverageService;

    private final CrateRepository crateRepository;
    private final BottleRepository bottleRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final OrderProperties orderProperties;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @Autowired
    public OrderService(UserService userService,
                        AddressService addressService,
                        BeverageService beverageService,
                        CrateRepository crateRepository,
                        BottleRepository bottleRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderProperties orderProperties) {
        this.userService = userService;
        this.addressService = addressService;
        this.beverageService = beverageService;

        this.crateRepository = crateRepository;
        this.bottleRepository = bottleRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;

        this.orderProperties = orderProperties;
    }

    public Order getOrderByOrderNumber(String orderNumber) throws NotFoundException {
        Optional<Order> orderOptional = orderRepository.findOrderByOrderNumber(orderNumber);

        if (orderOptional.isEmpty()) {
            throw new NotFoundException("No Order found with Order Number: " + orderNumber);
        }

        return orderOptional.get();
    }

    public Page<Order> getPagedOrdersByUsername(String username, int page) {
        return orderRepository.findAllByUserUsernameOrderByOrderNumber(username, PageRequest.of(page - 1, orderProperties.getPageSize()));
    }

    public List<OrderItem> getOrderItemsByOrderNumber(String orderNumber) {
        return orderItemRepository.findAllByOrderOrderNumber(orderNumber);
    }

    @Transactional
    public Order createOrder(String userName, Long deliveryAddressId, Long billingAddressId) throws NotFoundException {
        try {
            User user = userService.getUserByUserName(userName);
            Address deliveryAddress = addressService.getAddressById(deliveryAddressId);
            Address billingAddress = addressService.getAddressById(billingAddressId);

            Order order = new Order(null, null, LocalDate.now(), cartService.getCartTotal(), user, deliveryAddress, billingAddress, null);
            orderRepository.save(order);

            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem: cartService.getCartItems()) {
                int quantity = cartItem.getQuantity();
                Long beverageId = cartItem.getBeverageId();
                BeverageType beverageType = cartItem.getBeverageType();

                orderItems.add(buildOrderItem(order, beverageType, beverageId, quantity));

                if (beverageType == BeverageType.CRATE) {
                    crateRepository.decreaseQuantity(beverageId, quantity);
                } else if (beverageType == BeverageType.BOTTLE) {
                    bottleRepository.decreaseQuantity(beverageId, quantity);
                }
            }

            orderItemRepository.saveAll(orderItems);

            order.setOrderNumber(Helper.generateOrderNumber(order.getId()));
            orderRepository.save(order);

            cartService.clearCart();

            return order;
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    private OrderItem buildOrderItem(Order order, BeverageType beverageType, Long beverageId, int quantity) throws NotFoundException {
        Bottle bottle = beverageService.getBottleById(beverageId);
        Crate crate = beverageService.getCrateById(beverageId);

        return new OrderItem(
                null,
                beverageType,
                quantity,
                beverageType == BeverageType.BOTTLE
                        ? bottle
                        : null,
                beverageType == BeverageType.CRATE
                        ? crate
                        : null,
                order
        );
    }
}
