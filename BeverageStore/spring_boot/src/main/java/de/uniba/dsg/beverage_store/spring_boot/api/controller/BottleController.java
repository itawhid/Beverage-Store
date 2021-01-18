package de.uniba.dsg.beverage_store.spring_boot.api.controller;

import de.uniba.dsg.beverage_store.spring_boot.exception.NotFoundException;
import de.uniba.dsg.beverage_store.spring_boot.model.db.Bottle;
import de.uniba.dsg.beverage_store.spring_boot.model.dto.BeverageStockAddDTO;
import de.uniba.dsg.beverage_store.spring_boot.service.BeverageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/bottles")
public class BottleController {

    private final BeverageService beverageService;

    @Autowired
    public BottleController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @PatchMapping(value = "/{id}/stock",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addToStock(@PathVariable("id") Long id, @RequestBody @Valid BeverageStockAddDTO request, Errors errors) {
        log.info("Adding stock to the Bottle with ID: " + id + " - start");

        if (errors.hasErrors()) {
            log.info("Adding stock to the Bottle with ID: " + id + " - failed, found model error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(errors.getAllErrors()
                            .stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.joining(", ")));
        }

        try {
            Bottle bottle = beverageService.addStockToBottle(id, request.getQuantity());

            log.info("Adding stock to the Bottle with ID: " + id + " - completed");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(bottle);
        } catch (NotFoundException e) {
            log.info("Adding stock to the Bottle with ID: " + id + " - failed, found not found exception");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
