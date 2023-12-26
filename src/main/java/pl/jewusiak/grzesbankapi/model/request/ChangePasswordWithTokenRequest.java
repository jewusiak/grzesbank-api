package pl.jewusiak.grzesbankapi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.jewusiak.grzesbankapi.utils.validation.PasswordConstraint;

import java.util.UUID;

@Data
public class ChangePasswordWithTokenRequest {
    @NotNull
    private UUID token;
    @PasswordConstraint
    @NotBlank
    private String password;
}
