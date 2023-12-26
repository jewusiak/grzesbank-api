package pl.jewusiak.grzesbankapi.model.response;

import lombok.Data;

@Data
public class SensitiveUserDataResponse {
    private String pesel;
    private String documentNumber;
}
