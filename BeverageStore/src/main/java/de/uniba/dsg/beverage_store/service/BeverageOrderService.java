package de.uniba.dsg.beverage_store.service;

import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.helper.Helper;
import de.uniba.dsg.beverage_store.model.*;
import de.uniba.dsg.beverage_store.properties.BeverageOrderProperties;
import de.uniba.dsg.beverage_store.repository.BeverageOrderItemRepository;
import de.uniba.dsg.beverage_store.repository.BeverageOrderRepository;
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
public class BeverageOrderService {

    private final UserService userService;
    private final AddressService addressService;
    private final BeverageService beverageService;

    private final CrateRepository crateRepository;
    private final BottleRepository bottleRepository;
    private final BeverageOrderRepository beverageOrderRepository;
    private final BeverageOrderItemRepository beverageOrderItemRepository;

    private final BeverageOrderProperties beverageOrderProperties;

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @Autowired
    public BeverageOrderService(UserService userService,
                                AddressService addressService,
                                BeverageService beverageService,
                                CrateRepository crateRepository,
                                BottleRepository bottleRepository,
                                BeverageOrderRepository beverageOrderRepository,
                                BeverageOrderItemRepository beverageOrderItemRepository,
                                BeverageOrderProperties beverageOrderProperties) {
        this.userService = userService;
        this.addressService = addressService;
        this.beverageService = beverageService;

        this.crateRepository = crateRepository;
        this.bottleRepository = bottleRepository;
        this.beverageOrderRepository = beverageOrderRepository;
        this.beverageOrderItemRepository = beverageOrderItemRepository;

        this.beverageOrderProperties = beverageOrderProperties;
    }

    public BeverageOrder getBeverageOrderByOrderNumber(String orderNumber) throws NotFoundException {
        Optional<BeverageOrder> beverageOrderOptional = beverageOrderRepository.findBeverageOrderByOrderNumber(orderNumber);

        if (beverageOrderOptional.isEmpty()) {
            throw new NotFoundException("No Order found with Order Number: " + orderNumber);
        }

        return beverageOrderOptional.get();
    }

    public Page<BeverageOrder> getPagedBeverageOrdersByUsername(String username, int page) {
        return beverageOrderRepository.findAllByUserUsernameOrderByOrderNumber(username, PageRequest.of(page - 1, beverageOrderProperties.getPageSize()));
    }

    public List<BeverageOrderItem> getBeverageOrderItemsByOrderNumber(String orderNumber) {
        return beverageOrderItemRepository.findAllByBeverageOrderOrderNumber(orderNumber);
    }

    @Transactional
    public BeverageOrder createOrder(String userName, Long deliveryAddressId, Long billingAddressId) throws NotFoundException {
        try {
            User user = userService.getUserByUserName(userName);
            Address deliveryAddress = addressService.getAddressById(deliveryAddressId);
            Address billingAddress = addressService.getAddressById(billingAddressId);

            BeverageOrder beverageOrder = new BeverageOrder(null, null, LocalDate.now(), cartService.getCartTotal(), user, deliveryAddress, billingAddress, null);
            beverageOrderRepository.save(beverageOrder);

            List<BeverageOrderItem> beverageOrderItems = new ArrayList<>();
            for (CartItem cartItem: cartService.getCartItems()) {
                int quantity = cartItem.getQuantity();
                Long beverageId = cartItem.getBeverageId();
                BeverageType beverageType = cartItem.getBeverageType();

                beverageOrderItems.add(buildBeverageOrderItem(beverageOrder, beverageType, beverageId, quantity));

                if (beverageType == BeverageType.CRATE) {
                    crateRepository.decreaseQuantity(beverageId, quantity);
                } else if (beverageType == BeverageType.BOTTLE) {
                    bottleRepository.decreaseQuantity(beverageId, quantity);
                }
            }

            beverageOrderItemRepository.saveAll(beverageOrderItems);

            beverageOrder.setOrderNumber(Helper.generateOrderNumber(beverageOrder.getId()));
            beverageOrderRepository.save(beverageOrder);

            cartService.clearCart();

            return beverageOrder;
        } catch (Exception e) {
            throw new NotFoundException();
        }
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
