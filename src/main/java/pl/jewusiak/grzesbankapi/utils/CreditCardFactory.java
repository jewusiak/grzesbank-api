package pl.jewusiak.grzesbankapi.utils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.model.domain.CreditCard;
import pl.jewusiak.grzesbankapi.model.domain.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * A credit card number generator.
 *
 * @author Josef Galea
 */
@Component
public class CreditCardFactory {
    
    private static final String BIN = "4532";
    private static final int CCLENGTH = 16;
    
    private final Random random = new Random(System.currentTimeMillis());

    public CreditCard prepareCard(User user) {
        var cvv = String.valueOf(new Random().nextInt(100,1000));
        var validThru = LocalDate.now().plusYears(5).plusMonths(1).format(DateTimeFormatter.ofPattern("MM/yy"));
        var cn = generateCcn();
        return new CreditCard(null, cn, validThru, cvv, user);
    }
    
    private String generateCcn() {

        int randomNumberLength = CCLENGTH - (BIN.length() + 1);

        StringBuilder builder = new StringBuilder(BIN);
        for (int i = 0; i < randomNumberLength; i++) {
            int digit = this.random.nextInt(10);
            builder.append(digit);
        }

        // Do the Luhn algorithm to generate the check digit.
        int checkDigit = this.getCheckDigit(builder.toString());
        builder.append(checkDigit);

        return builder.toString();
    }

    /**
     * Generates the check digit required to make the given credit card number
     * valid (i.e. pass the Luhn check)
     *
     * @param number
     *            The credit card number for which to generate the check digit.
     * @return The check digit required to make the given credit card number
     *         valid.
     */
    private int getCheckDigit(String number) {

        // Get the sum of all the digits, however we need to replace the value
        // of the first digit, and every other digit, with the same digit
        // multiplied by 2. If this multiplication yields a number greater
        // than 9, then add the two digits together to get a single digit
        // number.
        //
        // The digits we need to replace will be those in an even position for
        // card numbers whose length is an even number, or those is an odd
        // position for card numbers whose length is an odd number. This is
        // because the Luhn algorithm reverses the card number, and doubles
        // every other number starting from the second number from the last
        // position.
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }

        // The check digit is the number required to make the sum a multiple of
        // 10.
        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
    }
}
