package de.uniba.dsg.beverage_store.spring_boot.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.ObjectError;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Helper {
    public static String generateOrderNumber(Long orderId) {
        LocalDate nowDate = LocalDate.now();

        return ("ORD" + (nowDate.getYear() % 100) +  String.format("%02d", nowDate.getMonthValue()) + String.format("%05d", orderId));
    }

    public static String encryptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static String constructErrorMessage(List<ObjectError> errors) {
        return errors
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }
}
