package com.szymonharabasz.grocerylistmanager.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final String specialCharacters = " !\"#$%&\\'()*+,-./:;[\\\\]^_`{|}~'";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        System.err.println("!!!! PASSWORD VALIDATOR HAS BEEN CALLED !!!!");
        String capitalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String smallLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "1234567890";
        return s.length() >= 8 &&
                 Arrays.stream(capitalLetters.split("")).anyMatch(s::contains) &&
                 Arrays.stream(smallLetters.split("")).anyMatch(s::contains) &&
                 Arrays.stream(digits.split("")).anyMatch(s::contains) &&
                 Arrays.stream(specialCharacters.split("")).anyMatch(s::contains);
    }
}
