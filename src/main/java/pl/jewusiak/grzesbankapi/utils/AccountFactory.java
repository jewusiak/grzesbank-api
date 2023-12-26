package pl.jewusiak.grzesbankapi.utils;

import org.springframework.stereotype.Component;
import pl.jewusiak.grzesbankapi.model.domain.Account;
import pl.jewusiak.grzesbankapi.model.domain.User;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Component
public class AccountFactory {

    private static final String BANK_ID = "67726573";
    private static final String ACCOUNT_SUFFIX = "252100";


    public Account prepareAccount(User user){
        return new Account(generateNo(), new BigDecimal(0), user);
    }
    
    private String generateNo() {
        var nextNumber = new Random().nextInt(Integer.MAX_VALUE);
        String accountNumber = BANK_ID + String.format("%16s", String.valueOf(nextNumber)).replace(' ', '0');
        var calcAccountNumber = new BigInteger(accountNumber + ACCOUNT_SUFFIX);
        var modulod = calcAccountNumber.mod(new BigInteger("97"));
        var control = new BigInteger("98").subtract(modulod);
        return control + accountNumber;
    }
}
