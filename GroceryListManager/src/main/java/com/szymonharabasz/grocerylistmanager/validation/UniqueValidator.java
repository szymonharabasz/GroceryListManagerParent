package com.szymonharabasz.grocerylistmanager.validation;

import com.szymonharabasz.grocerylistmanager.service.UserService;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValidator implements ConstraintValidator<Unique, String> {

    private final UserService userService;

    private String fieldName;

    @Inject
    public UniqueValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void initialize(final Unique unique) {
        fieldName = unique.name();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        System.err.println("!!!! UNIQUE VALIDATOR HAS BEEN CALLED !!!!");
        return !userService.findBy(fieldName, s).isPresent();
    }
}
