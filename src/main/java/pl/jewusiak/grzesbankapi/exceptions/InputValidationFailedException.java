package pl.jewusiak.grzesbankapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InputValidationFailedException extends ResponseStatusException {
    public InputValidationFailedException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
