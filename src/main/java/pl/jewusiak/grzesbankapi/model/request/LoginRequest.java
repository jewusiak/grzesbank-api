package pl.jewusiak.grzesbankapi.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import pl.jewusiak.grzesbankapi.utils.ValidationService;
import pl.jewusiak.grzesbankapi.utils.validation.PasswordConstraint;

import java.util.UUID;

@Data
public class LoginRequest {
    @NotNull
    private UUID pcid;
    @Pattern(regexp = ValidationService.emailRegex) 
    @NotBlank
    private String email;
    @Pattern(regexp = ValidationService.passwordRegex)
    @NotBlank
    private String password;
}
