package pl.jewusiak.grzesbankapi.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.mapper.ProfileMapper;
import pl.jewusiak.grzesbankapi.model.mapper.TransactionMapper;
import pl.jewusiak.grzesbankapi.model.response.AccountSummaryResponse;
import pl.jewusiak.grzesbankapi.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final ProfileMapper profileMapper;
    
    public AccountSummaryResponse getAccountSummaryForUser(User user) {
        return profileMapper.map(user);
    }
}
