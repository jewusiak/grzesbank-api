package pl.jewusiak.grzesbankapi.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.jewusiak.grzesbankapi.model.domain.Transaction;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.mapper.UserMapper;
import pl.jewusiak.grzesbankapi.model.request.RegistrationRequest;
import pl.jewusiak.grzesbankapi.service.AuthService;
import pl.jewusiak.grzesbankapi.service.UserService;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultDataGenerator {
    private final UserService userService;
    private final UserMapper userMapper;

    @Value("pl.jewusiak.grzesbankapi.business.bank_acn")
    private String bankAccountNumber;
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        createUser("Pierwszy", "1@m.pl");
        createUser("Drugi", "2@m.pl");
    }
    
    private void createUser(String lastname, String email) {
        if(userService.userExists(email)){
            var user = userService.getUser(email);
            log.info("User {} {} ({}, accn: {}) already exists. Skipping.",user.getFirstName(), user.getLastName(), user.getEmail(), user.getAccount().getAccountNumber());
            return;
        }
        
        var registrationRequest = RegistrationRequest.builder().address(User.Address.builder().street("10 Example St.").city("Warsaw").zipCode("00-001").build())
                .documentNumber("DOC 120120").pesel("12345654321").initialBalance(BigDecimal.valueOf(1000)).password("Mieszko1!")
                .firstName("Mieszko").lastName(lastname).email(email).build();
        User user = userMapper.map(registrationRequest);

        var transaction = Transaction.builder()
                .amount(BigDecimal.valueOf(1000))
                .senderAccountNumber(bankAccountNumber)
                .senderName("Grzesbank - 1st Branch")
                .senderAddress("Złota 10, Warsaw")
                .recipientName(user.getFirstName() + " " + user.getLastName())
                .recipientAddress(user.getAddress().toString())
                .executionTime(ZonedDateTime.now())
                .title("EXAMPLE USER - initial")
                .build();
        
        User cu = userService.createUser(user, transaction);
        
        log.info("Created user {} with accn {}.", cu.getEmail(), cu.getAccount().getAccountNumber());
    }
}
