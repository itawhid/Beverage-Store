package de.uniba.dsg.beverage_store.spring_boot.integration_test;

import de.uniba.dsg.beverage_store.spring_boot.TestHelper;
import de.uniba.dsg.beverage_store.spring_boot.service.InvoiceService;
import de.uniba.dsg.models.Invoice;
import de.uniba.dsg.models.InvoiceAddress;
import de.uniba.dsg.models.InvoiceItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    private final String BASE_PATH = "/api/invoice";

    @Test
    public void generateInvoice_success() throws Exception {
        Invoice requestBody = new Invoice(
                "ORD001",
                LocalDate.now(),
                "Customer 1",
                "customer1@email.com",
                new InvoiceAddress(
                        "Pestalozzistraße",
                        "9F",
                        "96052"),
                new InvoiceAddress(
                        "Kapellenstraße",
                        "23",
                        "96050"),
                Collections.singletonList(new InvoiceItem(
                        1,
                        "Pepsi",
                        "Bottle",
                        1,
                        1.0))
        );

        mockMvc.perform(TestHelper.createRestPostRequest(BASE_PATH, null, requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(TestHelper.createRestPostRequest(BASE_PATH, TestHelper.getManager(), requestBody))
                .andExpect(status().isCreated());

        mockMvc.perform(TestHelper.createRestPostRequest(BASE_PATH, TestHelper.getCustomer(), requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void generateInvoice_invalidData() throws Exception {
        Invoice requestBody = new Invoice(
                "",
                null,
                "",
                "",
                new InvoiceAddress(
                        "",
                        "",
                        ""),
                new InvoiceAddress(
                        "",
                        "",
                        ""),
                Collections.singletonList(new InvoiceItem(
                        0,
                        "",
                        "",
                        0,
                        0.0))
        );

        mockMvc.perform(TestHelper.createRestPostRequest(BASE_PATH, null, requestBody))
                .andExpect(status().isBadRequest());

        mockMvc.perform(TestHelper.createRestPostRequest(BASE_PATH, TestHelper.getManager(), requestBody))
                .andExpect(status().isBadRequest());

        mockMvc.perform(TestHelper.createRestPostRequest(BASE_PATH, TestHelper.getCustomer(), requestBody))
                .andExpect(status().isBadRequest());
    }
}
