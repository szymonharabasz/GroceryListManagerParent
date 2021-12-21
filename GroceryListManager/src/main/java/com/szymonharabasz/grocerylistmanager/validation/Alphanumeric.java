package com.szymonharabasz.grocerylistmanager.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = AlphanumericValidator.class)
public @interface Alphanumeric {
    String message() default "can contain only letters and digits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
