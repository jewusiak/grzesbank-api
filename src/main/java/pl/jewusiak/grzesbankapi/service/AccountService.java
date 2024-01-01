package pl.jewusiak.grzesbankapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.mapper.ProfileMapper;
import pl.jewusiak.grzesbankapi.model.response.AccountSummaryResponse;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final ProfileMapper profileMapper;
    
    public AccountSummaryResponse getAccountSummaryForUser(User user) {
        return profileMapper.map(user);
    }
}
