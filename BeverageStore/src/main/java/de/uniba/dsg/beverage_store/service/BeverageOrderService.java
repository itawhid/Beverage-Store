package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.helper.Helper;
import de.uniba.dsg.beverage_store.model.*;
import de.uniba.dsg.beverage_store.repository.BeverageOrderItemRepository;
import de.uniba.dsg.beverage_store.repository.BeverageOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BeverageOrderService {

    private final BeverageService beverageService;
    private final BeverageOrderRepository beverageOrderRepository;
    private final BeverageOrderItemRepository beverageOrderItemRepository;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @Autowired
    public BeverageOrderService(BeverageService beverageService, BeverageOrderRepository beverageOrderRepository, BeverageOrderItemRepository beverageOrderItemRepository) {
        this.beverageService = beverageService;
        this.beverageOrderRepository = beverageOrderRepository;
        this.beverageOrderItemRepository = beverageOrderItemRepository;
    }

    public BeverageOrder getBeverageOrderByOrderNumber(String orderNumber) throws NotFoundException {
        Optional<BeverageOrder> beverageOrderOptional = beverageOrderRepository.findBeverageOrderByOrderNumber(orderNumber);

        if (beverageOrderOptional.isEmpty()) {
            throw new NotFoundException("No Order found with Order Number: " + orderNumber);
        }

        return beverageOrderOptional.get();
    }

    public List<BeverageOrder> getBeverageOrdersByUsername(String username) {
        return beverageOrderRepository.findAllByUserUsernameOrderByOrderNumber(username);
    }

    public List<BeverageOrderItem> getBeverageOrderItemsByOrderNumber(String orderNumber) {
        return beverageOrderItemRepository.findAllByBeverageOrderOrderNumber(orderNumber);
    }

    public BeverageOrder createOrder(User user, Address deliveryAddress, Address billingAddress) throws NotFoundException {
        BeverageOrder beverageOrder = new BeverageOrder(null, null, LocalDate.now(), cartService.getCartTotal(), user, deliveryAddress, billingAddress, null);
        beverageOrderRepository.save(beverageOrder);

        List<BeverageOrderItem> beverageOrderItems = new ArrayList<>();
        for (CartItem cartItem: cartService.getCartItems()) {
            beverageOrderItems.add(buildBeverageOrderItem(beverageOrder, cartItem.getBeverageType(), cartItem.getBeverageId(), cartItem.getQuantity()));
        }

        beverageOrderItemRepository.saveAll(beverageOrderItems);

        beverageOrder.setOrderNumber(Helper.generateOrderNumber(beverageOrder.getId()));
        beverageOrderRepository.save(beverageOrder);

        cartService.clearCart();

        return beverageOrder;
    }

    private BeverageOrderItem buildBeverageOrderItem(BeverageOrder beverageOrder, BeverageType beverageType, Long beverageId, int quantity) throws NotFoundException {
        Bottle bottle = beverageService.getBottleById(beverageId);
        Crate crate = beverageService.getCrateById(beverageId);

        return new BeverageOrderItem(
                null,
                beverageType,
                quantity,
                beverageType == BeverageType.BOTTLE
                        ? bottle
                        : null,
                beverageType == BeverageType.CRATE
                        ? crate
                        : null,
                beverageOrder
        );
    }
}
