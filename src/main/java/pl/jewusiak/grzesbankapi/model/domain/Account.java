package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts_table")
public class Account {
    @Id
    private String accountNumber;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal balance;
    
    @OneToOne(optional = false)
    private User owner;
    
    public void changeBalance(BigDecimal change){
        balance = balance.add(change);
    }
}
