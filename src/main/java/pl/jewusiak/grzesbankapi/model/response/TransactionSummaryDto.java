package pl.jewusiak.grzesbankapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionSummaryDto {
    private ZonedDateTime date;
    private String contraSideName;
    private String title;
    private BigDecimal amount;
}
