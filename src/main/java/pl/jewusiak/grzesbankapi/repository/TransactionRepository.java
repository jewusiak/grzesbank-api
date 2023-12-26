package pl.jewusiak.grzesbankapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.jewusiak.grzesbankapi.model.domain.Transaction;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> getTransactionsBySenderAccountNumberOrRecipientAccountNumberOrderByExecutionTimeDesc(String senderAccountNumber, String recipientAccountNumber, Pageable pageable);
    
}
