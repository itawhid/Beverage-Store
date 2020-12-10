package de.uniba.dsg.beverage_store.controller;

import de.uniba.dsg.beverage_store.dto.AddCartItemDTO;
import de.uniba.dsg.beverage_store.exception.NotFoundException;
import de.uniba.dsg.beverage_store.model.CartItem;
import de.uniba.dsg.beverage_store.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/cart-items")
public class CartItemController {

    @Resource(name = "sessionScopedCartService")
    CartService cartService;

    @GetMapping(value = "/count")
    public ResponseEntity<Integer> getCount() {
        int cartItemCount = cartService.getCartItemCount();

        return new ResponseEntity<>(cartItemCount, HttpStatus.OK);
    }

    @GetMapping(value = "/total-price")
    public ResponseEntity<Double> getTotalPrice() {
        double cartTotal = cartService.getCartTotal();

        return new ResponseEntity<>(cartTotal, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCartItem(@RequestBody @Valid AddCartItemDTO addCartItemDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.join(", ", errors.getAllErrors()
                            .stream()
                            .map(x -> x.getDefaultMessage())
                            .collect(Collectors.toList())));
        }

        try {
            CartItem newCartItem = cartService.addCartItem(addCartItemDTO.getBeverageType(), addCartItemDTO.getBeverageId(), addCartItemDTO.getQuantity());
            System.out.println(newCartItem.getName() + '_' + newCartItem.getQuantity());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(newCartItem);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No " + addCartItemDTO.getBeverageType().name() + " found with ID: " + addCartItemDTO.getBeverageId());
        }
    }

    @DeleteMapping(value = "/{cart-item-id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable(name = "cart-item-id") Long cartItemId) {
        try {
            cartService.removeCartItem(cartItemId);

            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No Cart Item found with ID: " + cartItemId);
        }
    }
}
