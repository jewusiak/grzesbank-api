package pl.jewusiak.grzesbankapi.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.exceptions.TransactionRejected;
import pl.jewusiak.grzesbankapi.repository.AccountRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final AccountRepository accountRepository;
    @Value("pl.jewusiak.grzesbankapi.business.bank_acn")
    private String bankAccountNumber;    
    /**
     * Adds change to accN
     *
     * @param accN   account number
     * @param change change (added to the acc)
     */
    public void postBalanceChange(String accN, BigDecimal change) {
        if(accN.equals(bankAccountNumber)) return;
        accountRepository.findById(accN).ifPresentOrElse(account -> {
            account.changeBalance(change);
            accountRepository.save(account);
        }, () -> {
            throw new TransactionRejected("Unknown account %s".formatted(accN));
        });
    }

    public boolean hasSufficientFunds(String senderAccountNumber, BigDecimal amount) {
        if (senderAccountNumber.equals(bankAccountNumber)) return true;
        var opt = accountRepository.findById(senderAccountNumber).orElseThrow(() -> new TransactionRejected("Account %s does not exist.".formatted(senderAccountNumber)));
        return opt.getBalance().compareTo(amount) >= 0;
    }
}
