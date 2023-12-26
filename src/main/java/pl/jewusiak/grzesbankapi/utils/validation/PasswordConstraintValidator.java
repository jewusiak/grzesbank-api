package pl.jewusiak.grzesbankapi.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.jewusiak.grzesbankapi.exceptions.InputValidationFailedException;

import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<PasswordConstraint, String> {

    private static final Pattern password = Pattern.compile("^[A-Za-z0-9żźćńółęąśŻŹĆĄŚĘŁÓŃ\\-_ !@#$%^&*()+=\\[\\]{}:<>,./?]+$");
    private static final Pattern passwordAlphaBig = Pattern.compile("[A-ZŻŹĆĄŚĘŁÓŃ]+");
    private static final Pattern passwordAlphaSmall = Pattern.compile("[a-zżźćńółęąś]+");
    private static final Pattern passwordNumeric = Pattern.compile("[0-9]+");
    private static final Pattern passwordSpecial =
            Pattern.compile("[\\-_!@#$%^&*()+=\\[\\]{}:<>,./?]+");

    private boolean validatePassword(String pass) {
        return pass != null
                && pass.length() >= 8
                && password.matcher(pass).find()
                && passwordAlphaBig.matcher(pass).find()
                && passwordAlphaSmall.matcher(pass).find()
                && passwordNumeric.matcher(pass).find()
                && passwordSpecial.matcher(pass).find();

    }


    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validatePassword(value);
    }
}
