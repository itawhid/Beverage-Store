package de.uniba.dsg.beverage_store.validation.validator;

import de.uniba.dsg.beverage_store.validation.annotation.LaterThanOrEqualTo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class LaterThanOrEqualToValidator implements ConstraintValidator<LaterThanOrEqualTo, LocalDate> {

    private LocalDate value;

    @Override
    public void initialize(LaterThanOrEqualTo constraintAnnotation) {
        int year = Integer.parseInt(constraintAnnotation.year());
        int month = Integer.parseInt(constraintAnnotation.month());
        int dayOfMonth = Integer.parseInt(constraintAnnotation.dayOfMonth());

        value = LocalDate.of(year, month, dayOfMonth);
    }

    @Override
    public boolean isValid(LocalDate object, ConstraintValidatorContext constraintContext) {
        return object.isAfter(value) || object.isEqual(value);
    }
}
