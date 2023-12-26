package pl.jewusiak.grzesbankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionRejected extends ResponseStatusException {

    private TransactionRejected(HttpStatus status,  String reason) {
        super(status, reason);
    }
    
    public TransactionRejected(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
    
    public static TransactionRejected insufficientFunds() {
        return new TransactionRejected(HttpStatus.valueOf(418), "Insufficient funds");
    }
}
