package de.uniba.dsg.beverage_store.spring_boot.api.controller;

import de.uniba.dsg.beverage_store.spring_boot.helper.Helper;
import de.uniba.dsg.beverage_store.spring_boot.service.InvoiceService;
import de.uniba.dsg.models.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/api/invoice")
public class InvoiceRestController {

    private final InvoiceService invoiceService;

    public InvoiceRestController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateInvoice(@RequestBody @Valid Invoice invoice, Errors errors) {
        log.info("Generating invoice - start");

        if (errors.hasErrors()) {
            log.info("Generating invoice - failed, found model error");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Helper.constructErrorMessage(errors.getAllErrors()));
        }

        try {
            invoiceService.generateInvoice(invoice);

            log.info("Generating invoice - completed");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Invoice successfully generated and sent to the Email - " + invoice.getCustomerEmailId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error.");
        }
    }
}
