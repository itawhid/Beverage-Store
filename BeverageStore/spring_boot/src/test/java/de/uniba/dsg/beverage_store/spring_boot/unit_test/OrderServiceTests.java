package de.uniba.dsg.beverage_store.spring_boot.unit_test;

import de.uniba.dsg.beverage_store.spring_boot.demo.DemoData;
import de.uniba.dsg.beverage_store.spring_boot.exception.InsufficientStockException;
import de.uniba.dsg.beverage_store.spring_boot.exception.InvalidOperationException;
import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.model.BeverageType;
import de.uniba.dsg.beverage_store.spring_boot.model.db.*;
import de.uniba.dsg.beverage_store.spring_boot.repository.BottleRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.CrateRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.OrderItemRepository;
import de.uniba.dsg.beverage_store.spring_boot.repository.OrderRepository;
import de.uniba.dsg.beverage_store.spring_boot.service.CartService;
import de.uniba.dsg.beverage_store.spring_boot.service.FireStoreService;
import de.uniba.dsg.beverage_store.spring_boot.service.InvoiceService;
import de.uniba.dsg.beverage_store.spring_boot.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceTests {

    @Resource(name = "sessionScopedCartService")
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CrateRepository crateRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BottleRepository bottleRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private FireStoreService fireStoreService;

    @Test
    public void getOrderByOrderNumber_test() throws NotFoundException {
        BeverageOrder expectedOrder = DemoData.orders.stream()
                .findFirst()
                .orElse(null);

        assertNotNull(expectedOrder);

        BeverageOrder actualOrder = orderService.getOrderByOrderNumber(expectedOrder.getOrderNumber());

        assertEquals(expectedOrder.getId(), actualOrder.getId());
        assertEquals(expectedOrder.getOrderNumber(), actualOrder.getOrderNumber());

        assertThrows(NotFoundException.class, () -> orderService.getOrderByOrderNumber("Test Order Number"));
    }

    @Test
    public void getPagedOrders_test() {
        Page<BeverageOrder> firstPage = orderService.getPagedOrders(1, 2);
        Page<BeverageOrder> secondPage = orderService.getPagedOrders(2, 2);

        assertEquals(1, firstPage.getTotalPages());
        assertEquals(2, firstPage.getTotalElements());

        assertEquals(2, firstPage.stream().count());
        assertEquals(0, secondPage.stream().count());
    }

    @Test
    public void getPagedOrdersByUsername_test() {
        ApplicationUser user = getCustomer();

        assertNotNull(user);

        Page<BeverageOrder> firstPage = orderService.getPagedOrdersByUsername(user.getUsername(), 1, 2);
        Page<BeverageOrder> secondPage = orderService.getPagedOrdersByUsername(user.getUsername(), 2, 2);

        assertEquals(1, firstPage.getTotalPages());
        assertEquals(1, firstPage.getTotalElements());

        assertEquals(1, firstPage.stream().count());
        assertEquals(0, secondPage.stream().count());
    }

    @Test
    public void getOrdersByUsername_test() {
        ApplicationUser user = getCustomer();

        assertNotNull(user);

        List<BeverageOrder> expectedData = orderRepository.findAllByUserUsernameOrderByOrderNumber(user.getUsername());

        assertNotNull(user);

        List<BeverageOrder> actualData = orderService.getOrdersByUsername(user.getUsername());

        assertEquals(expectedData.size(), actualData.size());
    }

    @Test
    public void getOrderItemsByOrderNumber_test() {
        BeverageOrder order = DemoData.orders.stream()
                .findFirst()
                .orElse(null);

        assertNotNull(order);

        List<BeverageOrderItem> expectedData = orderItemRepository.findAllByOrderOrderNumber(order.getOrderNumber());

        List<BeverageOrderItem> actualData = orderService.getOrderItemsByOrderNumber(order.getOrderNumber());

        assertEquals(expectedData.size(), actualData.size());
    }

    @Test
    @Transactional
    public void createOrder_test() throws NotFoundException, InsufficientStockException, InvalidOperationException {
        int crateQuantity = 2;
        int bottleQuantity = 3;

        ApplicationUser user = getCustomer();

        assertNotNull(user);

        Address address = DemoData.addresses.stream()
                .filter(x -> x.getUser().getUsername().equals(user.getUsername()))
                .findFirst()
                .orElse(null);

        Crate crate = DemoData.crates.stream()
                .findFirst()
                .orElse(null);

        Bottle bottle = DemoData.bottles.stream()
                .findFirst()
                .orElse(null);

        assertNotNull(crate);
        assertNotNull(bottle);
        assertNotNull(address);

        cartService.addCartItem(BeverageType.CRATE, crate.getId(), crateQuantity);
        cartService.addCartItem(BeverageType.BOTTLE, bottle.getId(), bottleQuantity);

        double expectedCartTotal = cartService.getCartTotal();

        long orderCountBeforeAdd = orderRepository.count();
        long orderItemCountBeforeAdd = orderItemRepository.count();

        BeverageOrder addedOrder = orderService.createOrder(user.getUsername(), address.getId(), address.getId());

        assertEquals(0, cartService.getCartItemCount());
        assertEquals(expectedCartTotal, addedOrder.getPrice());

        assertEquals(2, orderItemRepository.findAllByOrderOrderNumber(addedOrder.getOrderNumber()).size());

        assertEquals(orderCountBeforeAdd + 1, orderRepository.count());
        assertEquals(orderItemCountBeforeAdd + 2, orderItemRepository.count());

        assertEquals((crate.getInStock() - crateQuantity), crateRepository.findById(crate.getId()).get().getInStock());
        assertEquals((bottle.getInStock() - bottleQuantity), bottleRepository.findById(bottle.getId()).get().getInStock());

        cartService.addCartItem(BeverageType.CRATE, crate.getId(), crateQuantity);
        cartService.addCartItem(BeverageType.BOTTLE, bottle.getId(), bottleQuantity);

        assertThrows(NotFoundException.class, () -> orderService.createOrder(user.getUsername(), 0L, 0L));

        cartService.clearCart();

        assertThrows(InvalidOperationException.class, () -> orderService.createOrder(user.getUsername(), address.getId(), address.getId()));
    }

    private ApplicationUser getCustomer() {
        return DemoData.applicationUsers.stream()
                .filter(x -> x.getRole() == Role.ROLE_CUSTOMER)
                .findFirst()
                .orElse(null);
    }
}
