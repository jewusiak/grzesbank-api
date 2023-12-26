package pl.jewusiak.grzesbankapi.model.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions_table")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull
    private String senderAccountNumber;
    
    private String senderName;
    
    private String senderAddress;
    
    @NotNull
    private String recipientAccountNumber;
    
    @NotNull
    private String recipientName;
    
    @NotNull
    private String recipientAddress;
    
    @CreatedDate
    private ZonedDateTime executionTime;
    
    private String title;
    
    @NotNull
    private BigDecimal amount;
}
