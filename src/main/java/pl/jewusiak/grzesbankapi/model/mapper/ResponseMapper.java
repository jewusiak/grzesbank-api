package pl.jewusiak.grzesbankapi.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.jewusiak.grzesbankapi.model.domain.CreditCard;
import pl.jewusiak.grzesbankapi.model.domain.SensitiveUserData;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.response.BasicUserInfoResponse;
import pl.jewusiak.grzesbankapi.model.response.SensitiveCcResponse;
import pl.jewusiak.grzesbankapi.model.response.SensitiveUserDataResponse;

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    
    @Mapping(source = "pesel", target = "pesel")
    @Mapping(source = "documentNumber", target = "documentNumber")
    SensitiveUserDataResponse map(SensitiveUserData domain);
    
    @Mapping(source = "cardNumber", target = "cardNumber")
    @Mapping(source = "validThru", target = "validity")
    @Mapping(source = "cvv", target = "cvv")
    SensitiveCcResponse map(CreditCard cc);

    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "surname", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "accountNumber", source = "account.accountNumber")
    BasicUserInfoResponse map(User user);
}
