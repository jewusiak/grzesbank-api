package pl.jewusiak.grzesbankapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountSummaryResponse {
    private String name;
    private String accountNumber;
    private String email;
    private BigDecimal balance;
    private Iterable<TransactionSummaryDto> lastTransactions;
}
