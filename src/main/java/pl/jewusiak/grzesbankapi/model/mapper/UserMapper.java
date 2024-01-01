package pl.jewusiak.grzesbankapi.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.model.request.RegistrationRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "address", target = "address")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "pesel", target = "sensitiveUserData.pesel")
    @Mapping(source = "documentNumber", target = "sensitiveUserData.documentNumber")
    User mapBasicData(RegistrationRequest registrationRequest);
}
