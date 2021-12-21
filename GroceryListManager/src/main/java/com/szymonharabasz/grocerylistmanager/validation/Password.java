package com.szymonharabasz.grocerylistmanager.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {
    String message() default "passwords must have at least 8 characters and contain at least one of each :" +
            "capital letter, small letter, digit and -special character !\"#$%&\\'()*+,-./:;[\\\\]^_`{|}~'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
