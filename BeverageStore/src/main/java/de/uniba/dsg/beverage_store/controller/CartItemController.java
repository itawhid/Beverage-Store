package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.model.dto.CartItemDTO;
import de.uniba.dsg.beverage_store.exception.InsufficientStockException;
import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.CartItem;
import de.uniba.dsg.beverage_store.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/cart-items")
public class CartItemController {

    @Resource(name = "sessionScopedCartService")
    CartService cartService;

    @GetMapping(value = "/count")
    public ResponseEntity<Integer> getCount() {
        log.info("Retrieving cart item count - start");

        int cartItemCount = cartService.getCartItemCount();

        log.info("Retrieving cart item count - completed");

        return new ResponseEntity<>(cartItemCount, HttpStatus.OK);
    }

    @GetMapping(value = "/total-price")
    public ResponseEntity<Double> getTotalPrice() {
        log.info("Retrieving cart item total - start");

        double cartTotal = cartService.getCartTotal();

        log.info("Retrieving cart item total - completed");

        return new ResponseEntity<>(cartTotal, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCartItem(@RequestBody @Valid CartItemDTO cartItemDTO, Errors errors) {
        log.info("Adding cart item - start");

        if (errors.hasErrors()) {
            log.info("Adding cart item - failed, found model error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.joining(", ")));
        }

        try {
            CartItem newCartItem = cartService.addCartItem(cartItemDTO.getBeverageType(), cartItemDTO.getBeverageId(), cartItemDTO.getQuantity());

            log.info("Adding cart item - completed");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(newCartItem);
        } catch (NotFoundException e) {
            log.info("Adding cart item - failed, found not found exception");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (InsufficientStockException e) {
            log.info("Adding cart item - failed, found insufficient stock exception");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{cart-item-id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable(name = "cart-item-id") Long cartItemId) {
        log.info("Removing cart item with ID: " + cartItemId + " - start");

        try {
            cartService.removeCartItem(cartItemId);

            log.info("Removing cart item with ID: " + cartItemId + " - completed");

            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (NotFoundException ex) {
            log.info("Removing cart item with ID: " + cartItemId + " - failed, found not found exception");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No Cart Item found with ID: " + cartItemId);
        }
    }
}
