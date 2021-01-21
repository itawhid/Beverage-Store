package de.uniba.dsg.cloudfunction.validation.annotation;

import de.uniba.dsg.cloudfunction.validation.validator.MoreThanZeroValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MoreThanZeroValidator.class)
@Documented
public @interface MoreThanZero {

    String message() default "Must be greater than zero";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    @Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        MoreThanZero[] value();
    }
}
