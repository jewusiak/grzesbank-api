package pl.jewusiak.grzesbankapi.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import pl.jewusiak.grzesbankapi.utils.validation.PasswordConstraint;

@Data
public class ChangePasswordRequest {
    @PasswordConstraint
    @NotBlank
    private String password;
}
