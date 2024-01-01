package pl.jewusiak.grzesbankapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jewusiak.grzesbankapi.model.domain.CreditCard;
import pl.jewusiak.grzesbankapi.repository.CreditCardRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    


    public CreditCard getById(UUID id) {
        return creditCardRepository.findById(id).orElseThrow();
    }
}
