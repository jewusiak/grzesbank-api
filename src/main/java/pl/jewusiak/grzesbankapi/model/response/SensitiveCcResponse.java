package pl.jewusiak.grzesbankapi.model.response;

import lombok.Data;

@Data
public class SensitiveCcResponse {
    private String cardNumber;
    private String validity;
    private String cvv;
}
