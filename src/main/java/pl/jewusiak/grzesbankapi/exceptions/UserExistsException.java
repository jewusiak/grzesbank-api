package pl.jewusiak.grzesbankapi.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class UserExistsException extends ResponseStatusException {

    public UserExistsException(String email) {
        super(HttpStatus.BAD_REQUEST);
        log.warn("User {} already exists.", email);
    }
}
