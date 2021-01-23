package de.uniba.dsg.cloudfunction;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.*;
import de.uniba.dsg.cloudfunction.models.Order;
import de.uniba.dsg.cloudfunction.models.OrderItem;

import javax.naming.directory.InvalidAttributesException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class InvoiceGeneratorFunction implements HttpFunction {

    private final String BUCKET_NAME = System.getenv("BUCKET_NAME");

    private static final Logger logger = Logger.getLogger(InvoiceGeneratorFunction.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws Exception {

        if (!request.getMethod().equalsIgnoreCase("POST")) {
            response.setStatusCode(HttpURLConnection.HTTP_FORBIDDEN);
            response.getWriter().write("Only POST request is permitted.");

            return;
        }

        try {
            Order order = getGsonObject().fromJson(request.getReader(), Order.class);

            validateOrder(order);

            Map<String, String> metadata = Map.ofEntries(
                    new AbstractMap.SimpleEntry<>("email", order.getCustomerEmailId()),
                    new AbstractMap.SimpleEntry<>("order_number", order.getOrderNumber()),
                    new AbstractMap.SimpleEntry<>("customer_name", order.getCustomerName())
            );

            InvoiceGenerator invoiceGenerator = new InvoiceGenerator(order, "invoice_template");

            createFileInCloudStorage(order.getOrderNumber() +  ".pdf", invoiceGenerator.generate(), metadata);

            invoiceGenerator.dispose();

            response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);

            logger.info("Generated invoice for Order: " + order.getOrderNumber());
        } catch (JsonSyntaxException | JsonIOException | InvalidAttributesException ex) {
            response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
            response.getWriter().write(ex.getMessage());

            logger.info("Exception: " + ex.getMessage());
        } catch (Exception ex) {
            response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);

            logger.info("Exception: " + ex.getMessage());
        }
    }

    private void validateOrder(Order order)
            throws InvalidAttributesException {

        List<String> errors = new ArrayList<>();

        errors.addAll(validate(order));
        errors.addAll(validate(order.getDeliveryAddress()));
        errors.addAll(validate(order.getBillingAddress()));

        for (OrderItem orderItem: order.getOrderItems()) {
            errors.addAll(validate(orderItem));
        }

        if (errors.isEmpty())
            return;

        throw new InvalidAttributesException(String.join("\n", errors));
    }

    private <T> List<String> validate(T obj) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        return validator.validate(obj)
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    private void createFileInCloudStorage(String fileName, byte[] fileBytes, Map<String, String> metadata) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setMetadata(metadata)
                .build();

        storage.create(blobInfo, fileBytes);
    }

    private Gson getGsonObject() {
        return new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonDeserializer) (json, type, jsonDeserializationContext)
                -> LocalDate.parse(json.getAsJsonPrimitive().getAsString())).create();
    }
}
