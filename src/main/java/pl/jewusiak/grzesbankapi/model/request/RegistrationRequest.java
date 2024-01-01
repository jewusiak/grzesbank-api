package pl.jewusiak.grzesbankapi.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import pl.jewusiak.grzesbankapi.model.domain.User;
import pl.jewusiak.grzesbankapi.utils.ValidationService;
import pl.jewusiak.grzesbankapi.utils.validation.PasswordConstraint;

import java.math.BigDecimal;

@Data
@Builder
public class RegistrationRequest {
    @PasswordConstraint
    @NotBlank
    private String password;
    @Pattern(regexp = ValidationService.plAlphaTextRegex)
    @NotBlank
    private String firstName;
    @Pattern(regexp = ValidationService.plAlphaTextRegex)
    @NotBlank
    private String lastName;
    @Pattern(regexp = ValidationService.emailRegex) 
    @NotBlank
    private String email;
    @Valid
    @NotNull
    private User.Address address;
    @Pattern(regexp = ValidationService.peselRegex)
    @NotBlank
    private String pesel;
    @Pattern(regexp = ValidationService.idNumberRegex)
    @NotBlank
    private String documentNumber;
    private BigDecimal initialBalance;
}
