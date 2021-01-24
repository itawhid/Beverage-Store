package de.uniba.dsg.beverage_store.spring_boot.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import de.uniba.dsg.models.Invoice;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class FireStoreService {

    private Firestore store;

    public FireStoreService() {
        try {
            File credentialsFile = ResourceUtils.getFile("classpath:firestore-key.json");

            FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance()
                    .toBuilder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsFile)))
                    .build();

            store = firestoreOptions.getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeOrder(Invoice invoice) {
        if (store == null)
            return;

        DocumentReference documentReference = store.collection("invoices")
                .document(invoice.getOrderNumber());

        Map<String, Object> orderMap = new HashMap<>();

        orderMap.put("order_number", invoice.getOrderNumber());
        orderMap.put("order_date", invoice.getOrderDate().toString());
        orderMap.put("customer_name", invoice.getCustomerName());
        orderMap.put("customer_email_id", invoice.getCustomerEmailId());
        orderMap.put("delivery_address", invoice.getDeliveryAddress());
        orderMap.put("billing_address", invoice.getBillingAddress());
        orderMap.put("items", invoice.getItems());

        documentReference.set(orderMap);
    }
}
