package pl.jewusiak.grzesbankapi.utils;

import org.springframework.stereotype.Component;
import pl.jewusiak.grzesbankapi.exceptions.InputValidationFailedException;

import java.util.regex.Pattern;

@Component
public class ValidationService {

    public static final String plAlphaTextRegex =
            "^[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\-]+$";
    public static final String plTextboxRegex =
            "^[0-9A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ\\-_/ ,]+$";
    public static final String emailRegex =
            "^[0-9A-Za-z\\-+~]+@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,}$";
    public static final String idNumberRegex = "^[A-Za-z]{3} ?[0-9]{6}$";
    public static final String plZipCodeRegex = "^[0-9]{2}-?[0-9]{3}$";
    public static final String ccyAmountRegex = "^[0-9]+(\\.[0-9]{1,2})?$";
    public static final String accNumberRegex = "^([0-9] ?){26}$";
    public static final String peselRegex = "^[0-9]{11}$";

    public static final String passwordRegex = "^[A-Za-z0-9żźćńółęąśŻŹĆĄŚĘŁÓŃ\\-_ !@#$%^&*()+=\\[\\]{}:<>,./?]+$";
    public static final Pattern password = Pattern.compile(passwordRegex);
    public static final Pattern passwordAlphaBig = Pattern.compile("[A-ZŻŹĆĄŚĘŁÓŃ]+");
    public static final Pattern passwordAlphaSmall = Pattern.compile("[a-zżźćńółęąś]+");
    public static final Pattern passwordNumeric = Pattern.compile("[0-9]+");
    public static final Pattern passwordSpecial =
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
}
