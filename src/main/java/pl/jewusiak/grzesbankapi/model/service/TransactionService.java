package pl.jewusiak.grzesbankapi.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.exceptions.TransactionRejected;
import pl.jewusiak.grzesbankapi.model.domain.Transaction;
import pl.jewusiak.grzesbankapi.model.mapper.TransactionMapper;
import pl.jewusiak.grzesbankapi.model.request.TransferOrderRequest;
import pl.jewusiak.grzesbankapi.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;

    public static final String BANK_ACN = "grzesbank-internal-acn";


    @Transactional(rollbackOn = {TransactionRejected.class})
    public void processTransaction(Transaction transaction) {
        transaction.setExecutionTime(ZonedDateTime.now());
        processTransactionInternal(transaction);
        transactionRepository.save(transaction);
    }

    private void processTransactionInternal(Transaction transaction) {
            if (!transaction.getSenderAccountNumber().equals(BANK_ACN) && !balanceService.hasSufficientFunds(transaction.getSenderAccountNumber(), transaction.getAmount())) {
                throw TransactionRejected.insufficientFunds();
            }
            if(transaction.getSenderAccountNumber().equals(transaction.getRecipientAccountNumber())){
                throw new TransactionRejected("Account numbers cannot be the same");
            }
            balanceService.postBalanceChange(transaction.getSenderAccountNumber(), transaction.getAmount().negate());
            balanceService.postBalanceChange(transaction.getRecipientAccountNumber(), transaction.getAmount());

    }

    public Iterable<Transaction> getLast5TransactionsForAccount(String accN) {
        var pageable = PageRequest.of(0, 5);
        var trs = transactionRepository.getTransactionsBySenderAccountNumberOrRecipientAccountNumberOrderByExecutionTimeDesc(accN, accN, pageable);
        List<Transaction> content = trs.getContent();
        content.forEach(t->{
            if(t.getSenderAccountNumber().equals(accN))
                t.setAmount(t.getAmount().multiply(new BigDecimal(-1)));
        });
        return content;
    }

    public Page<Transaction> getPagedTransactionsForAccount(String accN, Pageable pageable) {
        var trs = transactionRepository.getTransactionsBySenderAccountNumberOrRecipientAccountNumberOrderByExecutionTimeDesc(accN, accN, pageable);
        trs.getContent().forEach(t->{
            if(t.getSenderAccountNumber().equals(accN))
                t.setAmount(t.getAmount().multiply(new BigDecimal(-1)));
        });
        return trs;
    }
}
