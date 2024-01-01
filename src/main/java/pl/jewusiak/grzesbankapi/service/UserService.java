package pl.jewusiak.grzesbankapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.jewusiak.grzesbankapi.model.domain.Transaction;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public User getUser(String email) {
        return userRepository.findById(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public User getUser(Authentication authentication) {
        var email = authentication.getPrincipal().toString();
        return userRepository.findById(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User createUser(User user, Transaction transaction) {
        User userSaved = createUser(user);
        transaction.setRecipientAccountNumber(userSaved.getAccount().getAccountNumber());
        transactionService.processTransaction(transaction);
        return userSaved;
    }
    
    public boolean userExists(String email){
        return userRepository.existsById(email);
    }

}
