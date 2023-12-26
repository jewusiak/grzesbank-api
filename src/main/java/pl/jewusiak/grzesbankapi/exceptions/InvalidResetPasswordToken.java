package pl.jewusiak.grzesbankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidResetPasswordToken extends ResponseStatusException {
    public InvalidResetPasswordToken(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
