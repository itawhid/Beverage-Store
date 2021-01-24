package de.uniba.dsg.beverage_store.spring_boot.service;

import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.helper.Helper;
import de.uniba.dsg.beverage_store.spring_boot.model.BeverageType;
import de.uniba.dsg.beverage_store.spring_boot.model.CartItem;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Address;
import de.uniba.dsg.beverage_store.spring_boot.model.db.ApplicationUser;
import de.uniba.dsg.beverage_store.spring_boot.model.db.BeverageOrder;
import de.uniba.dsg.beverage_store.spring_boot.model.db.BeverageOrderItem;
import de.uniba.dsg.beverage_store.spring_boot.properties.OrderProperties;
import de.uniba.dsg.beverage_store.spring_boot.repository.BottleRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.CrateRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.OrderItemRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.OrderRepository;
import de.uniba.dsg.models.InvoiceAddress;
import de.uniba.dsg.models.Invoice;
import de.uniba.dsg.models.InvoiceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserService userService;
    private final AddressService addressService;
    private final BeverageService beverageService;
    private final FireStoreService fireStoreService;

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
                        FireStoreService fireStoreService,
                        CrateRepository crateRepository,
                        BottleRepository bottleRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        OrderProperties orderProperties) {
        this.userService = userService;
        this.addressService = addressService;
        this.beverageService = beverageService;
        this.fireStoreService = fireStoreService;

        this.crateRepository = crateRepository;
        this.bottleRepository = bottleRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;

        this.orderProperties = orderProperties;
    }

    public BeverageOrder getOrderByOrderNumber(String orderNumber) throws NotFoundException {
        Optional<BeverageOrder> orderOptional = orderRepository.findOrderByOrderNumber(orderNumber);

        if (orderOptional.isEmpty()) {
            throw new NotFoundException("No Order found with Order Number: " + orderNumber);
        }

        return orderOptional.get();
    }

    public Page<BeverageOrder> getPagedOrders(int page) {
        return orderRepository.findAllByOrderByOrderNumber(PageRequest.of(page - 1, orderProperties.getPageSize()));
    }

    public Page<BeverageOrder> getPagedOrdersByUsername(String username, int page) {
        return orderRepository.findAllByUserUsernameOrderByOrderNumber(username, PageRequest.of(page - 1, orderProperties.getPageSize()));
    }

    public List<BeverageOrder> getOrdersByUsername(String username) {
        return orderRepository.findAllByUserUsernameOrderByOrderNumber(username);
    }

    public List<BeverageOrderItem> getOrderItemsByOrderNumber(String orderNumber) {
        return orderItemRepository.findAllByOrderOrderNumber(orderNumber);
    }

    @Transactional
    public BeverageOrder createOrder(String userName, Long deliveryAddressId, Long billingAddressId) throws NotFoundException {
        ApplicationUser customer = userService.getUserByUserName(userName);
        Address deliveryAddress = addressService.getAddressById(deliveryAddressId);
        Address billingAddress = addressService.getAddressById(billingAddressId);

        BeverageOrder order = new BeverageOrder(null, null, LocalDate.now(), cartService.getCartTotal(), customer, deliveryAddress, billingAddress, null);
        orderRepository.save(order);

        int count = 0;
        List<BeverageOrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem: cartService.getCartItems()) {
            int quantity = cartItem.getQuantity();
            Long beverageId = cartItem.getBeverageId();
            BeverageType beverageType = cartItem.getBeverageType();

            orderItems.add(buildOrderItem(order, beverageType, beverageId, quantity, ++count));

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

        fireStoreService.storeOrder(constructInvoiceOrder(order, customer, deliveryAddress, billingAddress, orderItems));

        return order;
    }

    private BeverageOrderItem buildOrderItem(BeverageOrder order, BeverageType beverageType, Long beverageId, int quantity, int position) throws NotFoundException {
        return new BeverageOrderItem(
                null,
                beverageType,
                quantity,
                position,
                beverageType == BeverageType.BOTTLE
                        ? beverageService.getBottleById(beverageId)
                        : null,
                beverageType == BeverageType.CRATE
                        ? beverageService.getCrateById(beverageId)
                        : null,
                order
        );
    }

    private Invoice constructInvoiceOrder(BeverageOrder order, ApplicationUser customer, Address deliveryAddress, Address billingAddress, List<BeverageOrderItem> orderItems) {
        return new Invoice(
                order.getOrderNumber(),
                order.getDate(),
                customer.getFirstName() + " " + customer.getLastName(),
                null,
                new InvoiceAddress(
                        deliveryAddress.getStreet(),
                        deliveryAddress.getHouseNumber(),
                        deliveryAddress.getPostalCode()
                ),
                new InvoiceAddress(
                        billingAddress.getStreet(),
                        billingAddress.getHouseNumber(),
                        billingAddress.getPostalCode()
                ),
                orderItems.stream()
                        .map(x -> new InvoiceItem(
                                x.getPosition(),
                                x.getBeverageType() == BeverageType.BOTTLE
                                        ? x.getBottle().getName()
                                        : x.getCrate().getName(),
                                x.getBeverageType().name(),
                                x.getQuantity(),
                                x.getBeverageType() == BeverageType.BOTTLE
                                        ? x.getBottle().getPrice()
                                        : x.getCrate().getPrice()

                        ))
                        .collect(Collectors.toList())
        );
    }
}
