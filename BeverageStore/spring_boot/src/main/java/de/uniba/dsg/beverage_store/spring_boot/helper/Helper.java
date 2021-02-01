package de.uniba.dsg.beverage_store.spring_boot.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

public class Helper {
    public static String generateOrderNumber(Long orderId) {
        LocalDate nowDate = LocalDate.now();

        return ("ORD" + (nowDate.getYear() % 100) +  String.format("%02d", nowDate.getMonthValue()) + String.format("%05d", orderId));
    }

    public static String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
