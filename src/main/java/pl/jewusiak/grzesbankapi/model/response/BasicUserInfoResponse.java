package pl.jewusiak.grzesbankapi.model.response;

import lombok.Data;

@Data
public class BasicUserInfoResponse {
    private String firstName;
    private String surname;
    private String email;
    private String accountNumber;
}
