package pl.jewusiak.grzesbankapi.model.mapper;

import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import pl.jewusiak.grzesbankapi.model.domain.PasswordCombination;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.request.RegistrationRequest;
import pl.jewusiak.grzesbankapi.utils.AccountFactory;
import pl.jewusiak.grzesbankapi.utils.CreditCardFactory;
import pl.jewusiak.grzesbankapi.utils.PasswordCombinationsGenerator;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class UserMapper {

    @Autowired
    private PasswordCombinationsGenerator passwordCombinationsGenerator;
    @Autowired
    private AccountFactory accountFactory;
    @Autowired
    private CreditCardFactory creditCardFactory;

    @Mapping(source = "address", target = "address")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "pesel", target = "sensitiveUserData.pesel")
    @Mapping(source = "documentNumber", target = "sensitiveUserData.documentNumber")
    public abstract User map(RegistrationRequest registrationRequest);

    @AfterMapping
    void map(@MappingTarget User user, RegistrationRequest registrationRequest){
        List<PasswordCombination> combinations = passwordCombinationsGenerator.generatePasswordCombinations(registrationRequest.getPassword(), user);
        user.setPasswordCombinations(combinations);
        var account = accountFactory.prepareAccount(user);
        user.setAccount(account);
        var cc = creditCardFactory.prepareCard(user);
        user.setCreditCard(cc);
    }
}
