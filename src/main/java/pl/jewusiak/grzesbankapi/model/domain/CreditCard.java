package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import lombok.*;
import pl.jewusiak.grzesbankapi.utils.AesStringAttributeEncryptor;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {

    private final String BANK_IDENTIFICATION_NUMBER = "1234567890123456";
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Convert(converter = AesStringAttributeEncryptor.class)
    @Setter(AccessLevel.NONE)
    private String cardNumber;

    @Convert(converter = AesStringAttributeEncryptor.class)
    @Setter(AccessLevel.NONE)
    private String validThru;

    @Convert(converter = AesStringAttributeEncryptor.class)
    @Setter(AccessLevel.NONE)
    private String cvv;

    @OneToOne(optional = false)
    @Setter
    private User user;


}
